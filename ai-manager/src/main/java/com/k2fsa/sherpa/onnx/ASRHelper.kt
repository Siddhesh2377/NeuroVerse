package com.k2fsa.sherpa.onnx

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ASRHelper {
    const val SAMPLE_RATE = 16000
    const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    @SuppressLint("MissingPermission")
    fun createAudioRecord(): AudioRecord {
        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        return AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize * 2
        )
    }

    fun createVad(context: Context): Vad {
        val config = getVadModelConfig(0)!!
        return Vad(assetManager = context.assets, config = config)
    }

    fun createOfflineRecognizer(context: Context): OfflineRecognizer {
        val config = OfflineRecognizerConfig(
            featConfig = getFeatureConfig(SAMPLE_RATE, 80),
            modelConfig = getOfflineModelConfig(1)!!
        )
        return OfflineRecognizer(context.assets, config)
    }

    fun recordAndRecognize(
        audioRecord: AudioRecord,
        vad: Vad,
        offlineRecognizer: OfflineRecognizer,
        onResult: (String) -> Unit
    ): CoroutineScope {
        val scope = CoroutineScope(Dispatchers.IO)
        val buffer = ShortArray(512)

        scope.launch {
            audioRecord.startRecording()

            while (true) {
                val read = audioRecord.read(buffer, 0, buffer.size)
                if (read > 0) {
                    val floatSamples = FloatArray(read) { buffer[it] / 32768.0f }
                    vad.acceptWaveform(floatSamples)
                    while (!vad.empty()) {
                        val segment = vad.front()
                        launch {
                            val text = recognize(segment.samples, offlineRecognizer)
                            withContext(Dispatchers.Main) {
                                onResult(text)
                            }
                        }
                        vad.pop()
                    }
                }
            }
        }

        return scope
    }

    private fun recognize(samples: FloatArray, recognizer: OfflineRecognizer): String {
        val stream = recognizer.createStream()
        stream.acceptWaveform(samples, SAMPLE_RATE)
        recognizer.decode(stream)
        val result = recognizer.getResult(stream)
        stream.release()
        return result.text
    }

    fun stopRecording(scope: CoroutineScope, audioRecord: AudioRecord) {
        try {
            if (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                audioRecord.stop()
            }
            audioRecord.release()
        } catch (e: Exception) {
            Log.e("ASR", "Failed to stop AudioRecord", e)
        }
        scope.cancel()
    }



    fun saveAudioToFile(samples: ShortArray, file: File) {
        try {
            FileOutputStream(file).use { fos ->
                val buffer = ByteArray(samples.size * 2)
                for (i in samples.indices) {
                    buffer[i * 2] = (samples[i].toInt() and 0xFF).toByte()
                    buffer[i * 2 + 1] = ((samples[i].toInt() shr 8) and 0xFF).toByte()
                }
                fos.write(buffer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}
