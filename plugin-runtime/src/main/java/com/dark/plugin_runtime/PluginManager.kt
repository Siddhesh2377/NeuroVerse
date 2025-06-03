package com.dark.plugin_runtime

import android.content.Context
import android.util.Log
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class PluginManager(private val context: Context) {

    companion object {
        private const val TAG = "PluginManager"
    }


    private fun getPluginFolder(pluginName: String): File =
        File(context.getDir("plugins", Context.MODE_PRIVATE), pluginName)

    fun installPlugin(path: String, onResult: (pluginFolderPath: File) -> Unit) {

        val pluginFile = File(path)

        val pluginFolder = getPluginFolder(pluginFile.nameWithoutExtension)

        Log.d("PluginManager", "Plugin folder: $pluginFolder")
        pluginFolder.mkdirs()
        ZipInputStream(pluginFile.inputStream()).use { zip ->
            var entry: ZipEntry?
            var extractedAny = false

            while (zip.nextEntry.also { entry = it } != null) {
                val outFile = File(pluginFolder, entry!!.name)

                // ✅ Ensure directories exist
                outFile.parentFile?.mkdirs()

                // ✅ Write file
                zip.copyTo(outFile.outputStream())
                zip.closeEntry()

                extractedAny = true
            }

            if (!extractedAny) {
                Log.d(TAG, "❌ ZIP archive is empty — no files extracted.")
            } else {
                Log.d(TAG, "📦 Successfully extracted plugin")
                onResult(pluginFolder)
            }
        }


    }

}