package com.dark.neuroverse.services

import android.service.voice.VoiceInteractionService
import android.util.Log

class NeuroVService : VoiceInteractionService() {

    private val serviceName = "NeuroVService"

    override fun onReady() {
        super.onReady()
        Log.d(serviceName, "Service is ready")
    }

}