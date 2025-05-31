package com.dark.neuroverse.compose.screens.assistant

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dark.neuroverse.neurov.Command
import com.dark.neuroverse.neurov.executePrompt
import com.dark.neuroverse.neurov.mcp.engine.ActionRunner
import com.dark.neuroverse.neurov.mcp.voice.AudioRecorder
import com.dark.neuroverse.neurov.mcp.voice.STT
import com.dark.neuroverse.ui.theme.NeuroVerseTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("MissingPermission")
@Composable
fun AssistantScreen(
    onClickOutside: () -> Unit,
    onActionCompleted: () -> Unit
) {
    val context = LocalContext.current
    val cacheFile = remember { File(context.cacheDir, "input_audio.raw") }
    val recorder = remember { AudioRecorder() }
    val stt = remember { STT(context) }

    var userPrompt by remember { mutableStateOf("") }
    var displayMessage by remember { mutableStateOf("Hello User, I am here to help you navigate your phone with ease") }
    var isListening by remember { mutableStateOf(false) }
    var isRecognizerReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        stt.initRecognizer { ready -> isRecognizerReady = ready }
    }

    LaunchedEffect(isListening) {
        if (!isRecognizerReady) return@LaunchedEffect
        if (isListening) {
            recorder.start(cacheFile) {
                isListening = false
                Log.d("AudioRecorder", "Recording finished")
                processAudioAndExecute(cacheFile, stt, context, onActionCompleted) { _, message ->
                    displayMessage = message
                }
            }
        }
    }

    NeuroVerseTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClickOutside)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(24.dp)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Neuro V",
                        style = MaterialTheme.typography.displaySmall,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = userPrompt,
                        onValueChange = { userPrompt = it },
                        label = { Text("Ask something...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = displayMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = FontFamily.Serif,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    ListenButton(
                        isListening = isListening,
                        onToggleListening = { listening -> isListening = listening }
                    )
                }
            }
        }
    }
}

private fun processAudioAndExecute(
    audioFile: File,
    stt: STT,
    context: Context,
    onActionCompleted: () -> Unit,
    onResult: (Command?, String) -> Unit
) {
    stt.transcribeAudioFile(audioFile) { result ->
        if (result.isNotBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                executePrompt(result, context) { cmd ->
                    val message = cmd?.let { command ->
                        if (command.action == "open_app") "Opening App: ${command.app}"
                        else "Running Command"
                    } ?: ""

                    CoroutineScope(Dispatchers.Main).launch {
                        onResult(cmd, message)
                    }

                    if (cmd != null) {
                        ActionRunner(cmd, context).execute { action, output ->
                            Log.d("ActionRunner", "Action: $action, Output: $output")
                            onActionCompleted()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ListenButton(
    isListening: Boolean,
    onToggleListening: (Boolean) -> Unit
) {
    Button(
        onClick = { onToggleListening(!isListening) },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        val transition = updateTransition(targetState = isListening, label = "ListeningTransition")
        val scale by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 500) },
            label = "ScaleAnimation"
        ) { state -> if (state) 1.1f else 1f }
        val alpha by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 500) },
            label = "AlphaAnimation"
        ) { state -> if (state) 1f else 0.5f }

        Text(
            text = if (isListening) "Stop Listening" else "Listen",
            modifier = Modifier.graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                alpha = alpha
            ),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}