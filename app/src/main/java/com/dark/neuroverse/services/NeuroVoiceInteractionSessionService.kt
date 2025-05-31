package com.dark.neuroverse.services

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService
import android.util.Log
import com.dark.neuroverse.activities.AssistantActivity

class NeuroVoiceInteractionSessionService : VoiceInteractionSessionService() {
    override fun onNewSession(args: Bundle?): VoiceInteractionSession {
        Log.d("NeuroSessionService", "onNewSession called")
        return NeuroSession(this)
    }
}


class NeuroSession(service: Context) : VoiceInteractionSession(service) {

    override fun onShow(args: Bundle?, showFlags: Int) {
        super.onShow(args, showFlags)
        Log.d("NeuroSession", "Assistant UI shown")
        Intent(context, AssistantActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }
    }

    override fun onHide() {
        super.onHide()
        Log.d("NeuroSession", "Assistant UI hidden")
    }
}
