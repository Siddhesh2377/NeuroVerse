package com.dark.neuroverse

import android.app.Application
import android.util.Log
import com.dark.ai_manager.ai.local.Neuron
import com.dark.ai_manager.ai.local.STT
import com.dark.ai_manager.ai.local.TTS
import com.dark.plugin_runtime.engine.PluginManager

import kotlinx.coroutines.*

class NeuroVerseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("NeuroVerseApplication", "✅ Application started")
        PluginManager.init(applicationContext)
    }
}
