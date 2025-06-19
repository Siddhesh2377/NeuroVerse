package com.dark.ai_manager.ai.local

import android.content.Context
import android.media.*
import android.net.Uri
import kotlinx.coroutines.*
import java.io.File
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.k2fsa.sherpa.onnx.OfflineTts
import com.k2fsa.sherpa.onnx.getOfflineTtsConfig

object TTS {
    private val sttScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    var sid by mutableStateOf("30")
    var speed by mutableStateOf("0.8")
    var isPlayingAudio by mutableStateOf(false)
    var isGenerating by mutableStateOf(false)

    private var tts: OfflineTts? = null
    private var track: AudioTrack? = null
    private var mediaPlayer: MediaPlayer? = null

    fun initialize(context: Context) {
        if (tts != null) return  // Prevent re-initialization

        val config = getOfflineTtsConfig(
            modelDir = "vits-vctk",
            modelName = "vits-vctk.int8.onnx",
            lexicon = "lexicon.txt"
        )

        tts = OfflineTts(context.assets, config)

        val sampleRate = tts!!.sampleRate()
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val attr = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()

        val format = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()

        track = AudioTrack(attr, format, bufferSize, AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE)
    }

    fun generate(context: Context, text: String) {
        val sidInt = sid.toIntOrNull()
        val speedFloat = speed.toFloatOrNull()
        if (sidInt == null || speedFloat == null || speedFloat <= 0f || text.isBlank()) return

        isGenerating = true
        val filePath = File(context.filesDir, "generated.wav").absolutePath

        sttScope.launch {
            val callback = TtsCallback { floatSamples ->
                try {
                    val shortSamples = ShortArray(floatSamples.size) {
                        (floatSamples[it] * Short.MAX_VALUE)
                            .toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                            .toShort()
                    }

                    track?.let {
                        if (it.playState != AudioTrack.PLAYSTATE_PLAYING) {
                            it.play()
                        }
                        it.write(shortSamples, 0, shortSamples.size, AudioTrack.WRITE_BLOCKING)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                1
            }

            try {
                val result = tts?.generateWithCallback(text, sidInt, speedFloat, callback)
                result?.save(filePath)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isGenerating = false
            }
        }
    }

    fun generateWithOutAudio(context: Context, text: String) {
        val sidInt = sid.toIntOrNull()
        val speedFloat = speed.toFloatOrNull()
        if (sidInt == null || speedFloat == null || speedFloat <= 0f || text.isBlank()) return

        isGenerating = true
        val filePath = File(context.filesDir, "generated.wav").absolutePath

        sttScope.launch {
            try {
                val result = tts?.generate(text, sidInt, speedFloat)
                result?.save(filePath)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isGenerating = false
            }
        }
    }

    fun play(context: Context) {
        val file = File(context.filesDir, "generated.wav")
        if (!file.exists()) return

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, Uri.fromFile(file)).apply {
            setOnCompletionListener {
                isPlayingAudio = false
                release()
                mediaPlayer = null
            }
            start()
            isPlayingAudio = true
        }
    }

    fun stop() {
        mediaPlayer?.run {
            stop()
            release()
        }
        mediaPlayer = null

        track?.apply {
            pause()
            flush()
        }

        isPlayingAudio = false
    }
}

class TtsCallback(private val onSamples: (FloatArray) -> Int) : (FloatArray) -> Int {
    override fun invoke(samples: FloatArray): Int = onSamples(samples)
}
