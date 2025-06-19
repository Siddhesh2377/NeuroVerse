package com.dark.ai_manager.ai.local

import android.content.Context
import android.media.AudioRecord
import android.util.Log
import com.k2fsa.sherpa.onnx.*
import kotlinx.coroutines.*

object STT {
    private val sttScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var vad: Vad? = null
    private var recognizer: OfflineRecognizer? = null
    private var audioRecord: AudioRecord? = null
    private var recordScope: CoroutineScope? = null

    var isRecording = false
        private set

    fun initialize(context: Context) {
        if (vad == null) vad = ASRHelper.createVad(context)
        if (recognizer == null) recognizer = ASRHelper.createOfflineRecognizer(context)
    }

    fun start(context: Context, onResult: (String) -> Unit) {
        if (isRecording) return

        initialize(context)

        val currentVad = vad ?: return
        val currentRecognizer = recognizer ?: return

        audioRecord = ASRHelper.createAudioRecord()
        isRecording = true

        recordScope = ASRHelper.recordAndRecognize(
            audioRecord = audioRecord!!,
            vad = currentVad,
            offlineRecognizer = currentRecognizer
        ) { result ->
            onResult(result)
        }
    }

    fun stop() {
        if (!isRecording) return

        try {
            audioRecord?.let { ASRHelper.stopRecording(recordScope!!, it) }
        } catch (e: Exception) {
            Log.e("STT", "Failed to stop recording", e)
        }

        audioRecord = null
        recordScope = null
        isRecording = false
    }

    fun release() {
        stop()
        recognizer?.release()
        recognizer = null
        vad = null
    }
}
