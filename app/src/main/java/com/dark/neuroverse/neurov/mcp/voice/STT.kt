package com.dark.neuroverse.neurov.mcp.voice


class STT{

}


//import android.annotation.SuppressLint
//import android.content.Context
//import android.media.AudioFormat
//import android.media.AudioRecord
//import android.media.MediaRecorder
//import android.os.SystemClock
//import android.util.Log
//import com.google.gson.JsonParser
//import org.vosk.Model
//import org.vosk.Recognizer
//import org.vosk.android.StorageService
//import java.io.File
//import java.io.FileInputStream
//import java.io.FileOutputStream
//import java.io.IOException
//import kotlin.math.sqrt
//
//@SuppressLint("MissingPermission")
//class STT(private val context: Context) {
//    private lateinit var model: Model
//    private var isReady = false
//
//    fun initRecognizer(onReady: (Boolean) -> Unit) {
//        StorageService.unpack(
//            context,
//            "model-en-us",
//            "model",
//            object : StorageService.Callback<Model> {
//                override fun onComplete(model: Model) {
//                    this@STT.model = model
//                    isReady = true
//                    onReady(true)
//                }
//            },
//            object : StorageService.Callback<IOException> {
//                override fun onComplete(exception: IOException) {
//                    Log.e("Vosk", "Model unpack failed: $exception")
//                    isReady = false
//                    onReady(false)
//                }
//            }
//        )
//    }
//
//    fun transcribeAudioFile(file: File, onResult: (String) -> Unit) {
//        if (!isReady) {
//            onResult("")
//            return
//        }
//
//        Thread {
//            try {
//                val recognizer = Recognizer(model, 16000.0f)
//                val inputStream = FileInputStream(file)
//                val buffer = ByteArray(4096)
//
//                while (true) {
//                    val bytesRead = inputStream.read(buffer)
//                    if (bytesRead <= 0) break
//                    recognizer.acceptWaveForm(buffer, bytesRead)
//                }
//
//                inputStream.close()
//                val resultJson = recognizer.result
//                val result = JsonParser.parseString(resultJson).asJsonObject.get("text").asString
//                Log.d("VoskFinal", result)
//                onResult(result)
//            } catch (e: Exception) {
//                Log.e("VoskTranscribe", "Error: ${e.message}")
//                onResult("")
//            }
//        }.start()
//    }
//}
//
//class AudioRecorder() {
//    private val sampleRate = 16000
//    private val bufferSize = AudioRecord.getMinBufferSize(
//        sampleRate,
//        AudioFormat.CHANNEL_IN_MONO,
//        AudioFormat.ENCODING_PCM_16BIT
//    )
//
//    @SuppressLint("MissingPermission")
//    private val audioRecord = AudioRecord(
//        MediaRecorder.AudioSource.MIC,
//        sampleRate,
//        AudioFormat.CHANNEL_IN_MONO,
//        AudioFormat.ENCODING_PCM_16BIT,
//        bufferSize
//    )
//
//    private var recordingThread: Thread? = null
//    private var isRecording = false
//
//    fun start(outputFile: File, silenceTimeoutMs: Long = 2500, onResult: () -> Unit) {
//        isRecording = true
//        audioRecord.startRecording()
//
//        recordingThread = Thread {
//            val buffer = ByteArray(bufferSize)
//            val outputStream = FileOutputStream(outputFile)
//            val startTime = SystemClock.elapsedRealtime()
//            var lastVoiceTime = startTime
//            val maxDurationMs = 10000
//
//            // Warm-up: measure background noise for 300ms
//            var backgroundRms = 0.0
//            val warmUpEnd = startTime + 300
//            var warmUpCount = 0
//
//            while (isRecording) {
//                val read = audioRecord.read(buffer, 0, buffer.size)
//                if (read > 0) {
//                    outputStream.write(buffer, 0, read)
//
//                    val rms = calculateRms(buffer, read)
//
//                    // During warm-up, calculate average background RMS
//                    val now = SystemClock.elapsedRealtime()
//                    if (now < warmUpEnd) {
//                        backgroundRms += rms
//                        warmUpCount++
//                        continue
//                    } else if (warmUpCount > 0 && backgroundRms > 0) {
//                        backgroundRms /= warmUpCount
//                    }
//
//                    // Voice detection threshold
//                    val dynamicThreshold = backgroundRms + 300
//
//                    if (rms > dynamicThreshold) {
//                        lastVoiceTime = now
//                    }
//
//                    // Stop if silent for too long
//                    if (now - lastVoiceTime > silenceTimeoutMs) {
//                        Log.d("AudioRecorder", "Silence detected, stopping")
//                        break
//                    }
//
//                    // Failsafe max duration
//                    if (now - startTime > maxDurationMs) {
//                        Log.d("AudioRecorder", "Max duration reached, stopping")
//                        break
//                    }
//                }
//            }
//
//            isRecording = false
//            audioRecord.stop()
//            outputStream.close()
//
//            onResult()
//        }.apply { start() }
//    }
//
//
//
//    private fun calculateRms(buffer: ByteArray, read: Int): Double {
//        var sum = 0.0
//        var i = 0
//
//        // Avoid index out of bounds for odd length
//        while (i + 1 < read) {
//            val low = buffer[i].toInt() and 0xFF
//            val high = buffer[i + 1].toInt()
//            val sample = (high shl 8) or low
//            val normalized = if (sample > 32767) sample - 65536 else sample // handle signed 16-bit
//            sum += normalized * normalized
//            i += 2
//        }
//
//        val rms = sqrt(sum / (read / 2.0))
//        return rms
//    }
//
//
//
//    fun stop() {
//        isRecording = false
//        audioRecord.stop()
//        recordingThread?.join()
//        audioRecord.release()
//    }
//}
