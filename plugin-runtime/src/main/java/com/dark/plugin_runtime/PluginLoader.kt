package com.dark.plugin_runtime

import android.content.Context
import android.util.Log
import com.dark.plugin_api.info.Plugin
import com.dark.plugin_runtime.engine.model.PluginInfo
import dalvik.system.DexClassLoader
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class PluginLoader(private val context: Context) {

    companion object {
        private const val TAG = "PluginLoader"
    }

    private fun getPluginFolder(pluginName: String): File =
        File(context.getDir("plugins", Context.MODE_PRIVATE), pluginName)

    private fun copyAndExtractPluginZip(pluginName: String, pluginFolder: File) {
        val pluginZipFile = File(pluginFolder, "plugin.zip")

        // ✅ Copy plugin.zip from assets to app storage
        context.assets.open("$pluginName.zip").use { input ->
            pluginZipFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        Log.d(TAG, "📦 Copied plugin.zip to: ${pluginZipFile.absolutePath}")

        // ✅ Extract contents of plugin.zip
        ZipInputStream(pluginZipFile.inputStream()).use { zip ->
            var entry: ZipEntry?
            while (zip.nextEntry.also { entry = it } != null) {
                val outFile = File(pluginFolder, entry!!.name)

                // ✅ Ensure directories exist
                outFile.parentFile?.mkdirs()

                // ✅ Write file
                zip.copyTo(outFile.outputStream())
                zip.closeEntry()
                Log.d(TAG, "📄 Extracted: ${entry!!.name}")
            }
        }
    }

    fun extractPlugin(pluginName: String) {
        val pluginFolder = getPluginFolder(pluginName)

        // ✅ Cleanup before extracting
        if (pluginFolder.exists()) {
            pluginFolder.deleteRecursively()
        }
        pluginFolder.mkdirs()

        copyAndExtractPluginZip(pluginName, pluginFolder)
        Log.i(TAG, "✅ Plugin extracted to: ${pluginFolder.absolutePath}")
    }

    fun loadPlugin(pluginName: String, onResult: (PluginInfo) -> Unit): Plugin {
        val pluginFolder = getPluginFolder(pluginName)

        // ✅ Extract only if not already extracted
        if (!pluginFolder.exists() || !File(pluginFolder, "plugin.jar").exists()) {
            extractPlugin(pluginName)
        }

        val pluginJar = File(pluginFolder, "plugin.jar")
        val manifestFile = File(pluginFolder, "manifest.json")

        if (!pluginJar.exists()) throw FileNotFoundException("❌ plugin.jar not found at $pluginJar")
        if (!manifestFile.exists()) throw FileNotFoundException("❌ manifest.json not found at $manifestFile")

        // ✅ Copy to a safe read-only location
        val safeJar = File(context.noBackupFilesDir, "$pluginName-readonly.jar")
        pluginJar.copyTo(safeJar, overwrite = true)
        safeJar.setReadOnly()

        val manifest = JSONObject(manifestFile.readText())
        val mainClassName = manifest.getString("main")
        val permissions = (0 until manifest.getJSONArray("permissions").length())
            .map { manifest.getJSONArray("permissions").getString(it) }


        Log.i(TAG, "✅ Permissions: $permissions")

        val optimizedDir = context.getDir("dex_opt", Context.MODE_PRIVATE)

        val classLoader = DexClassLoader(
            safeJar.absolutePath,
            optimizedDir.absolutePath,
            null,
            context.classLoader
        )

        val clazz = classLoader.loadClass(mainClassName)
        val constructor = clazz.getDeclaredConstructor(Context::class.java)
        val instance = constructor.newInstance(context)

        Log.d("Instance", "Instance: $instance")

        if (instance !is Plugin) {
            throw IllegalStateException("❌ $mainClassName does not implement Plugin interface")
        }

        onResult(PluginInfo(1, "plugin", permissions, mainClassName, instance))

        Log.i(TAG, "✅ Loaded plugin class: ${instance.getName()}")
        return instance
    }

}
