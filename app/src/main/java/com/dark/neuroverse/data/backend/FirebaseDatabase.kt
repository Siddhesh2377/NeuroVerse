package com.dark.neuroverse.data.backend

import android.content.Context
import android.util.Log
import androidx.core.content.FileProvider
import com.dark.neuroverse.data.models.PluginLink
import com.dark.plugin_runtime.PluginManager
import com.dark.plugin_runtime.database.installed_plugin_db.PluginInstalledDatabase
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

fun fetchAllPlugins(onResult: (List<PluginLink>) -> Unit) {
    val db = Firebase.firestore
    db.collection("plugins")
        .get()
        .addOnSuccessListener { result ->
            val pluginList = result.mapNotNull { it.toObject(PluginLink::class.java) }
            Log.d("Firebase", "✅ Fetch successful : $pluginList")
            onResult(pluginList)
        }
        .addOnFailureListener { e ->
            Log.e("Firebase", "❌ Fetch failed", e)
            onResult(emptyList())
        }
}

fun downloadAndInstall(
    plugin: PluginLink,
    context: Context,
    db: PluginInstalledDatabase,
    onSuccess: (() -> Unit)? = null,
    onFailure: ((String) -> Unit)? = null
) {
    val scope = CoroutineScope(Dispatchers.IO)
    val pluginManager = PluginManager(context)

    scope.launch {
        val alreadyInstalled = db.pluginDao().getPluginByName(plugin.name) != null
        if (alreadyInstalled) {
            withContext(Dispatchers.Main) {
                onFailure?.invoke("⚠️ Plugin \"${plugin.name}\" is already installed.")
            }
            return@launch
        }

        try {
            // Download the plugin .zip
            val url = URL(plugin.downloadUrl)
            val connection = url.openConnection()
            connection.connect()

            val input = connection.getInputStream()
            val file = File(context.cacheDir, "${plugin.name}.zip")
            val output = FileOutputStream(file)

            input.copyTo(output)
            input.close()
            output.close()

            Log.d("PluginDownload", "✅ Downloaded to ${file.absolutePath}")

            // Install the plugin from the downloaded .zip
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            pluginManager.installPlugin(uri) { pluginData ->
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("PluginInstall", "✅ Installed to ${pluginData.manifestFile}")
                    val rowId = db.pluginDao().insertPlugin(pluginData)
                    if (rowId != -1L) {
                        withContext(Dispatchers.Main) { onSuccess?.invoke() }
                    } else {
                        withContext(Dispatchers.Main) {
                            onFailure?.invoke("⚠️ Plugin was installed but already exists in DB.")
                        }
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("PluginInstall", "❌ Download/Install failed: ${e.message}")
            withContext(Dispatchers.Main) { onFailure?.invoke("❌ Failed: ${e.message}") }
        }
    }
}
