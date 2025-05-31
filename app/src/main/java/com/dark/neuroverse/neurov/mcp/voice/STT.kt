package com.dark.neuroverse.neurov.mcp.voice

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.SystemClock
import android.util.Log
import com.google.gson.JsonParser
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.StorageService
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.sqrt

@SuppressLint("MissingPermission")
class STT(private val context: Context) {
    private lateinit var model: Model
    private var isReady = false

    fun initRecognizer(onReady: (Boolean) -> Unit) {
        StorageService.unpack(
            context,
            "model-en-in",
            "model",
            object : StorageService.Callback<Model> {
                override fun onComplete(model: Model) {
                    this@STT.model = model
                    isReady = true
                    onReady(true)
                }
            },
            object : StorageService.Callback<IOException> {
                override fun onComplete(exception: IOException) {
                    Log.e("Vosk", "Model unpack failed: $exception")
                    isReady = false
                    onReady(false)
                }
            }
        )
    }

    fun transcribeAudioFile(file: File, onResult: (String) -> Unit) {
        if (!isReady) {
            onResult("")
            return
        }

        Thread {
            try {
                val recognizer = Recognizer(model, 16000.0f)
                val inputStream = FileInputStream(file)
                val buffer = ByteArray(4096)

                while (true) {
                    val bytesRead = inputStream.read(buffer)
                    if (bytesRead <= 0) break
                    recognizer.acceptWaveForm(buffer, bytesRead)
                }

                inputStream.close()
                val resultJson = recognizer.result
                val result = JsonParser.parseString(resultJson).asJsonObject.get("text").asString
                Log.d("VoskFinal", result)
                onResult(result)
            } catch (e: Exception) {
                Log.e("VoskTranscribe", "Error: ${e.message}")
                onResult("")
            }
        }.start()
    }
}

class AudioRecorder() {
    private val sampleRate = 16000
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    @SuppressLint("MissingPermission")
    private val audioRecord = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        bufferSize
    )

    private var recordingThread: Thread? = null
    private var isRecording = false

    fun start(outputFile: File, silenceTimeoutMs: Long = 4000, onResult: () -> Unit) {
        isRecording = true
        audioRecord.startRecording()

        recordingThread = Thread {
            val buffer = ByteArray(bufferSize)
            val outputStream = FileOutputStream(outputFile)
            var lastVoiceTime = SystemClock.elapsedRealtime()

            while (isRecording) {
                val read = audioRecord.read(buffer, 0, buffer.size)
                if (read > 0) {
                    outputStream.write(buffer, 0, read)

                    // Calculate RMS of the audio buffer
                    val rms = calculateRms(buffer, read)

                    // If RMS is above threshold, reset silence timer
                    if (rms > 1000) { // You can tune this threshold
                        lastVoiceTime = SystemClock.elapsedRealtime()
                    }

                    // If silence duration exceeded, stop recording
                    if (SystemClock.elapsedRealtime() - lastVoiceTime > silenceTimeoutMs) {
                        Log.d("AudioRecorder", "Silence detected, stopping recording")
                        break
                    }
                }
            }

            isRecording = false
            audioRecord.stop()
            outputStream.close()

            onResult() // Only call after stop + close
        }.apply { start() }
    }


    private fun calculateRms(buffer: ByteArray, read: Int): Double {
        var sum = 0.0
        var i = 0
        while (i < read) {
            val sample = (buffer[i + 1].toInt() shl 8) or (buffer[i].toInt() and 0xFF)
            sum += sample * sample
            i += 2
        }
        return sqrt(sum / (read / 2.0))
    }


    fun stop() {
        isRecording = false
        audioRecord.stop()
        recordingThread?.join()
        audioRecord.release()
    }
}
