package com.dark.plugin_runtime.engine

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import com.dark.plugin_api.info.plugin.Plugin
import com.dark.plugin_runtime.database.installed_plugin_db.PluginInstalledDatabase
import com.dark.plugin_runtime.model.PluginModel
import com.dark.plugin_runtime.utils.queryFileName
import dalvik.system.DexClassLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

@SuppressLint("StaticFieldLeak")
object PluginManager {

    private val pluginScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context.applicationContext // safe!
    }
    private const val TAG = "PluginManager"


    private val db = PluginInstalledDatabase.getInstance(context)

    private fun getPluginFolder(pluginName: String): File =
        File(context.getDir("plugins", Context.MODE_PRIVATE), pluginName)

    private fun installPlugin(
        uri: Uri,
        onInstallationStarted: () -> Unit = {},
        onError: (Exception) -> Unit = {},
        onInstallationComplete: (pluginInfo: PluginModel) -> Unit
    ) {
        pluginScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Load All The Details
                    val rawFileName = queryFileName(uri, context) ?: "plugin_${System.currentTimeMillis()}.zip"
                    val pluginName = rawFileName.substringBeforeLast('.')
                    val pluginFolder = getPluginFolder(pluginName).apply { mkdirs() }

                    val inputStream = context.contentResolver.openInputStream(uri)
                    ZipInputStream(inputStream).use { zip ->
                        var entry: ZipEntry?
                        var extractedAny = false
                        withContext(Dispatchers.Main) { onInstallationStarted() }

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

                            val pluginInfo = readPluginInfo(pluginName)
                            db.pluginDao().insertPlugin(pluginInfo)

                            withContext(Dispatchers.Main) {
                                onInstallationComplete(pluginInfo)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to install plugin", e)
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }

    private fun readPluginInfo(pluginName: String): PluginModel {
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
        return PluginModel(
            pluginName = pluginName,
            pluginDescription = pluginDis,
            pluginPermissions = permissions,
            mainClass = mainClassName,
            manifestFile = manifestFilePath.path,
            pluginPath = pluginFolder.path,
            pluginApi = pluginApiVersion
        )
    }

    fun unInstallPlugin(name: String, onResult: (Boolean) -> Unit) {
        pluginScope.launch {
            withContext(Dispatchers.IO) {
                val path = db.pluginDao().getPluginFolderByName(name)
                val file = File(path)
                Log.d("PluginManager", "❌ Deleting plugin at: $path")
                val result = if (file.exists()) {
                    val deleted = file.deleteRecursively()
                    if (deleted) {
                        Log.d("PluginManager", "✅ Plugin deleted: $path")
                        db.pluginDao().deletePlugin(path)
                        true
                    } else {
                        Log.e("PluginManager", "❌ Failed to delete plugin at: $path")
                        false
                    }
                } else {
                    Log.w("PluginManager", "⚠️ Plugin path does not exist: $path")
                    false
                }

                withContext(Dispatchers.Main) {
                    onResult(result)
                }
            }
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