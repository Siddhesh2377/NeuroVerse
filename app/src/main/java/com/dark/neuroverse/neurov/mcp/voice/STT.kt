package com.dark.neuroverse.neurov.mcp.voice

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.StorageService
import java.io.IOException


@SuppressLint("MissingPermission")
class STT(private val context: Context) {
    private lateinit var model: Model
    private var recognizer: Recognizer? = null
    private var isEverythingOK = false
    private var listeningThread: Thread? = null

    private val audioRecord = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        16000,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        2048
    )

    fun initRecognizer(onReady: (Boolean) -> Unit) {
        StorageService.unpack(
            context,
            "model-en-in",
            "model",
            object : StorageService.Callback<Model> {
                override fun onComplete(model: Model) {
                    this@STT.model = model
                    recognizer = Recognizer(model, 16000.0f)
                    isEverythingOK = true
                    onReady(true)
                }
            },
            object : StorageService.Callback<IOException> {
                override fun onComplete(exception: IOException) {
                    Log.e("Vosk", "Model unpack failed: $exception")
                    isEverythingOK = false
                    onReady(false)
                }
            }
        )
    }

    fun startListening(onResult: (String) -> Unit) {
        if (!isEverythingOK || audioRecord.state != AudioRecord.STATE_INITIALIZED) return

        if (listeningThread?.isAlive == true) return

        audioRecord.startRecording()

        listeningThread = Thread {
            val buffer = ByteArray(4096)
            try {
                while (!Thread.interrupted()) {
                    val read = audioRecord.read(buffer, 0, buffer.size)
                    if (read > 0 && recognizer?.acceptWaveForm(buffer, read) == true) {
                        val result = recognizer?.result
                        Log.d("Vosk", "Result: $result")
                        onResult(result.toString())
                    }
                }
            } catch (e: Exception) {
                Log.e("Vosk", "Error in audio thread: ${e.message}")
            }
        }.apply { start() }
    }

    fun stopListening() {
        try {
            listeningThread?.interrupt()
            listeningThread = null
            audioRecord.stop()
        } catch (e: Exception) {
            Log.e("Vosk", "Stop failed: ${e.message}")
        }
    }

    fun release() {
        try {
            stopListening()
            audioRecord.release()
        } catch (e: Exception) {
            Log.e("Vosk", "Release error: ${e.message}")
        }
    }

    fun reset() {
        recognizer?.reset()
    }
}
