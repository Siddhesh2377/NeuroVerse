package com.dark.neuroverse

import android.app.Application
import android.util.Log
import com.dark.ai_manager.ai.local.STT
import com.dark.ai_manager.ai.local.TTS
import com.dark.plugin_runtime.engine.PluginManager

import kotlinx.coroutines.*

class NeuroVerseApplication : Application(), CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.Default) {

    override fun onCreate() {
        super.onCreate()
        Log.d("NeuroVerseApplication", "✅ Application started")
        PluginManager.init(applicationContext)

        async {
            STT.initialize(applicationContext)
            TTS.initialize(applicationContext)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        cancel() // Cancel coroutine scope to prevent leaks
    }
}
