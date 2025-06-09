package com.dark.neuroverse.services

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log
import com.dark.plugin_runtime.engine.PluginManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PluginSandboxService : Service() {

    companion object {
        const val MSG_RUN_PLUGIN = 1
        const val TAG = "PluginSandbox"
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder {
        return handlerMessenger.binder

    }

    private val handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            MSG_RUN_PLUGIN -> {
                val pluginName = msg.data.getString("plugin_name")
                Log.d(TAG, "\uD83D\uDE80 Running plugin in sandbox: $pluginName")

                if (pluginName != null) {
                    coroutineScope.launch {
                        try {
                            PluginManager.runPlugin(pluginName) { plugin ->
                                plugin.onStart()
                            }
                            val reply = Message.obtain(null, MSG_RUN_PLUGIN).apply {
                                data = Bundle().apply {
                                    putString("status", "✅ Plugin ran safely")
                                }
                            }
                            msg.replyTo.send(reply)
                        } catch (e: Exception) {
                            Log.e(TAG, "❌ Error running plugin", e)
                            val reply = Message.obtain(null, MSG_RUN_PLUGIN).apply {
                                data = Bundle().apply {
                                    putString("status", "❌ ${e.message}")
                                }
                            }
                            msg.replyTo.send(reply)
                        }
                    }
                }
            }
        }
        true
    }

    private val handlerMessenger = Messenger(handler)
}
