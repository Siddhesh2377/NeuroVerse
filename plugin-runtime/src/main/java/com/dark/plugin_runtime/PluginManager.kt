package com.dark.plugin_runtime

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.dark.plugin_api.info.Plugin
import com.dark.plugin_runtime.database.installed_plugin_db.InstalledPluginModel
import com.dark.plugin_runtime.database.installed_plugin_db.PluginInstalledDatabase
import com.dark.plugin_runtime.engine.PluginExecutionManager
import dalvik.system.DexClassLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class PluginManager(private val context: Context) {

    companion object {
        private const val TAG = "PluginManager"
    }

    private fun getPluginFolder(pluginName: String): File =
        File(context.getDir("plugins", Context.MODE_PRIVATE), pluginName)

    fun installPlugin(uri: Uri, onResult: (pluginInfo: InstalledPluginModel) -> Unit) {
        val rawFileName = queryFileName(uri) ?: "plugin_${System.currentTimeMillis()}.zip"
        val pluginName = rawFileName.substringBeforeLast('.')  // ✅ Strip .zip
        val pluginFolder = getPluginFolder(pluginName.substringBeforeLast('.'))

        pluginFolder.mkdirs()

        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            ZipInputStream(inputStream).use { zip ->
                var entry: ZipEntry?
                var extractedAny = false

                while (zip.nextEntry.also { entry = it } != null) {
                    val outFile = File(pluginFolder, entry!!.name)

                    if (entry.isDirectory) {
                        outFile.mkdirs()
                    } else {
                        outFile.parentFile?.mkdirs()
                        zip.copyTo(outFile.outputStream())
                        zip.closeEntry()
                        extractedAny = true
                    }
                }

                if (!extractedAny) {
                    Log.d(TAG, "❌ ZIP archive is empty.")
                } else {
                    Log.d(TAG, "📦 Successfully extracted plugin")
                    onResult(readPluginInfo(pluginName))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to install plugin", e)
        }
    }

    fun readPluginInfo(pluginName: String): InstalledPluginModel {
        val pluginFolder = getPluginFolder(pluginName)
        val manifestFilePath = File(pluginFolder, "manifest.json")
        if (!manifestFilePath.exists()) throw FileNotFoundException("❌ manifest.json not found at $manifestFilePath")
        Log.d(TAG, "✅ Found manifest.json at: ${manifestFilePath.absolutePath}")
        val manifest = JSONObject(manifestFilePath.readText())
        val pluginName = manifest.getString("name")
        val pluginDis = manifest.getString("description")
        val mainClassName = manifest.getString("main")
        val pluginApiVersion = manifest.getString("plugin-api-version")
        val permissions = (0 until manifest.getJSONArray("permissions").length())
            .map { manifest.getJSONArray("permissions").getString(it) }
        return InstalledPluginModel(
            pluginName = pluginName,
            pluginDescription = pluginDis,
            pluginPermissions = permissions,
            mainClass = mainClassName,
            manifestFile = manifestFilePath.path,
            pluginPath = pluginFolder.path,
            pluginApi = pluginApiVersion
        )
    }

    private fun queryFileName(uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null) ?: return null
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        val name = if (nameIndex >= 0) cursor.getString(nameIndex) else null
        cursor.close()
        return name
    }

    fun unInstallPlugin(path: String, onResult: (boolean: Boolean) -> Unit) {
        val file = File(path)
        Log.d("PluginManager", "❌ Deleting plugin at: $path")
        if (file.exists()) {
            val deleted = file.deleteRecursively()
            if (deleted) {
                Log.d("PluginManager", "✅ Plugin deleted: $path")
                onResult(true)
            } else {
                Log.e("PluginManager", "❌ Failed to delete plugin at: $path")
                onResult(false)
            }
        } else {
            Log.w("PluginManager", "⚠️ Plugin path does not exist: $path")
            onResult(false)
        }
    }

    fun runPlugin(pluginName: String, onResult: (Plugin) -> Unit) {
        var db = PluginInstalledDatabase.getInstance(context)
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val pluginFolder = db.pluginDao().getPluginFolderByName(pluginName)

            val pluginJar = File(pluginFolder, "plugin.jar")
            val mainClass = db.pluginDao().getMainClassByName(pluginName)
            if (!pluginJar.exists()) throw FileNotFoundException("❌ plugin.jar not found at $pluginJar")

            //Copying the JAR file to a READ-ONLY folder : no dex can be executed in the WRITEABLE folder
            val safeJar = File(context.noBackupFilesDir, "$pluginName-readonly.jar")
            pluginJar.copyTo(safeJar, overwrite = true)
            safeJar.setReadOnly()

            val classLoader = DexClassLoader(
                safeJar.absolutePath,
                null,
                null,
                context.classLoader
            )

            val clazz = classLoader.loadClass(mainClass)
            val constructor = clazz.getDeclaredConstructor(Context::class.java)
            val instance = constructor.newInstance(context)


            if (instance !is Plugin) {
                throw IllegalStateException("❌ $mainClass does not implement Plugin interface")
            }
            Log.i(TAG, "✅ Loaded plugin class: ${instance.getName()}")
            onResult(instance)
            PluginExecutionManager.launchPlugin(instance)
        }
    }

}