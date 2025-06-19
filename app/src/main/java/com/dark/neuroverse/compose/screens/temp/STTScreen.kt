package com.dark.neuroverse.compose.screens.temp

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.k2fsa.sherpa.onnx.GeneratedAudio
import com.k2fsa.sherpa.onnx.OfflineTts
import com.k2fsa.sherpa.onnx.getOfflineTtsConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun STTScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val assetManager = context.assets

    var tts by remember { mutableStateOf<OfflineTts?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var track by remember { mutableStateOf<AudioTrack?>(null) }

    var text by remember { mutableStateOf("Hey There, I am Neuro V, Here to Help You with the setup.") }
    var sid by remember { mutableStateOf("30") }
    var speed by remember { mutableStateOf("0.8") }
    var isPlaying by remember { mutableStateOf(false) }
    var isGenerating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val config = getOfflineTtsConfig(
            modelDir = "vits-vctk",
            modelName = "vits-vctk.int8.onnx",
            lexicon = "lexicon.txt",
        )
        tts = OfflineTts(assetManager, config)
        val sampleRate = tts!!.sampleRate()
        val bufLength = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_FLOAT
        )
        val attr = AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .setUsage(AudioAttributes.USAGE_MEDIA).build()
        val format = AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO).setSampleRate(sampleRate).build()
        track = AudioTrack(
            attr,
            format,
            bufLength,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )
        track?.play()
    }

    Column(modifier = Modifier
        .padding(paddingValues)
        .padding(16.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Text") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = sid,
            onValueChange = { sid = it },
            label = { Text("Speaker ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = speed,
            onValueChange = { speed = it },
            label = { Text("Speed") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                val sidInt = sid.toIntOrNull()
                val speedFloat = speed.toFloatOrNull()
                if (sidInt == null || speedFloat == null || speedFloat <= 0f || text.isBlank()) {
                    Toast.makeText(context, "Please fill all fields correctly", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }

                isGenerating = true
                val filePath = File(context.filesDir, "generated.wav").absolutePath
                track?.pause()
                track?.flush()
                track?.play()

                coroutineScope.launch(Dispatchers.IO) {
                    val callback = TtsCallback { samples ->
                        track?.write(samples, 0, samples.size, AudioTrack.WRITE_BLOCKING)
                        1
                    }

                    val result: GeneratedAudio? =
                        tts?.generateWithCallback(text, sidInt, speedFloat, callback)
                    result?.save(filePath)

                    isGenerating = false
                }
            }, enabled = !isGenerating) {
                Text("Generate")
            }

            Button(onClick = {
                val filePath = File(context.filesDir, "generated.wav")
                if (!filePath.exists()) return@Button
                mediaPlayer?.stop()
                mediaPlayer = MediaPlayer.create(context, Uri.fromFile(filePath))
                mediaPlayer?.start()
                isPlaying = true
            }, enabled = !isGenerating) {
                Text("Play")
            }

            Button(onClick = {
                mediaPlayer?.stop()
                mediaPlayer = null
                track?.pause()
                track?.flush()
                isPlaying = false
            }, enabled = isPlaying) {
                Text("Stop")
            }
        }
    }
}

// Define this outside @Composable scope
class TtsCallback(private val onSamples: (FloatArray) -> Int) : (FloatArray) -> Int {
    override fun invoke(samples: FloatArray): Int = onSamples(samples)
}
