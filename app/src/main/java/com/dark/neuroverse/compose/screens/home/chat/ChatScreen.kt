package com.dark.neuroverse.compose.screens.home.chat

import android.media.AudioRecord
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Send
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LoadingIndicatorDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.component1
import com.dark.neuroverse.R
import com.dark.neuroverse.compose.components.RichText
import com.dark.neuroverse.utils.rememberAudioPermissionState
import com.k2fsa.sherpa.onnx.ASRHelper.createAudioRecord
import com.k2fsa.sherpa.onnx.ASRHelper.createOfflineRecognizer
import com.k2fsa.sherpa.onnx.ASRHelper.createVad
import com.k2fsa.sherpa.onnx.ASRHelper.recordAndRecognize
import com.k2fsa.sherpa.onnx.ASRHelper.stopRecording
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel


@Composable
fun ChatScreen(paddingValues: PaddingValues) {
    val (hasPermission, requestPermission) = rememberAudioPermissionState()

    AnimatedContent(
        targetState = hasPermission.value,
        label = "Permission Switch"
    ) { granted ->
        if (!granted) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Microphone permission required to use this feature.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        requestPermission
                        Log.d("Permission", "Requesting permission")
                    }) {
                        Text("Grant Permission")
                    }
                }
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Header()
                Body(Modifier.weight(1f))
                BottomBar()
            }
        }
    }
}


@Composable
fun Header() {
    var deleteButton by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                "NeuroV Chat",
                style = MaterialTheme.typography.displaySmall,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.weight(1f))

            if (deleteButton) {
                IconButton(
                    onClick = {

                    },
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        Icons.Outlined.Settings,
                        "settings",
                        modifier = Modifier
                            .size(26.dp)
                    )
                }
            }

        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RichText(
                "Privacy Focused **Offline AI**\n" +
                        "On Your Device",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Light
            )

            Icon(painterResource(R.drawable.shield), "protected data")
        }
    }
}

@Composable
fun Body(modifier: Modifier) {
    Card(
        modifier = modifier.padding(bottom = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BottomBar() {
    val context = LocalContext.current
    var resultText by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }

    val vad = remember { createVad(context) }
    val recognizer = remember { createOfflineRecognizer(context) }

    var audioRecord by remember { mutableStateOf<AudioRecord?>(null) }
    var scope by remember { mutableStateOf<CoroutineScope?>(null) }
    var startAudio by remember { mutableStateOf(false) }

    LaunchedEffect(startAudio) {
        if (startAudio) {
            audioRecord = createAudioRecord()
            scope = recordAndRecognize(
                audioRecord = audioRecord!!, // now safely unwrapped
                vad = vad,
                offlineRecognizer = recognizer
            ) { r ->
                resultText += r
                Log.d("Audio", "Result: $r")
                text = resultText
            }
        } else {
            scope?.let {
                Log.d("Audio", "Stopping recording")
                audioRecord?.let { record ->
                    stopRecording(it, record)
                }
                it.cancel()
                scope = null
                audioRecord = null
            }
        }
    }


    // Main container (mimics the Card with a pill shape)
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 34.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .padding(start = 16.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(min = 100.dp, max = 250.dp)
                        .padding( 8.dp)
                ) {
                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        singleLine = false,
                        decorationBox = { innerTextField ->
                            if (text.isEmpty()) {
                                Text(
                                    "Say Anything...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    )
                }


                IconButton(
                    onClick = {
                        startAudio = !startAudio
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {

                    AnimatedContent(startAudio, label = "Audio Animated") { it ->

                        when (it) {
                            true -> {
                                val (a, r, g, b) = listOf(
                                    Color.White.alpha,
                                    Color.Red.alpha,
                                    Color.Green.alpha,
                                    Color.Blue.alpha
                                )

                                LoadingIndicator(color = LoadingIndicatorDefaults.containedIndicatorColor.copy(a, r, g, b))
                            }

                            false -> {


                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Mic"
                                )
                            }
                        }

                    }

                }

                IconButton(
                    onClick = {



                    }, colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    AnimatedContent(text.isNotEmpty(), label = "Audio Animated") { it ->

                        when (it) {
                            true -> {
                                Icon(
                                    imageVector = Icons.AutoMirrored.TwoTone.Send,
                                    contentDescription = "Audio"
                                )
                            }
                            false -> {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Audio"
                                )
                            }
                        }

                    }

                }
            }
        }
    }
}
