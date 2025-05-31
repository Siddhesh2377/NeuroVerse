// Final integrated version of STT + AudioRecorder with Compose AssistantScreen

package com.dark.neuroverse.compose.screens.assistant

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("MissingPermission")
@Composable
fun AssistantScreen(onClickOutside: () -> Unit, onActionCompleted: () -> Unit) {
    val context = LocalContext.current
    val cacheFile = File(context.cacheDir, "input_audio.raw")
    val recorder = remember { AudioRecorder() }
    val stt = remember { STT(context) }

    var userPrompt by remember { mutableStateOf("") }
    var fullMessage by remember { mutableStateOf("Waiting for input...") }
    var animatedMessage by remember { mutableStateOf("Hello User I am Here To Help You navigate Your PHONE with ease") }
    var isListening by remember { mutableStateOf(false) }
    var isRecognizerReady by remember { mutableStateOf(false) }
    var command: Command? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        stt.initRecognizer { isRecognizerReady = it }
    }

    LaunchedEffect(isListening) {
        if (isListening) {
            recorder.start(cacheFile){
                isListening = false
                Log.d("AudioRecorder", "Recording finished")
                stt.transcribeAudioFile(cacheFile) { result ->
                    userPrompt = result
                    isListening = false
                }
            }
        }else{
            stt.transcribeAudioFile(cacheFile) { result ->
                userPrompt = result
                isListening = false
            }
        }
    }

    NeuroVerseTheme {
        Box(Modifier.fillMaxSize().clickable { onClickOutside() }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(24.dp)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Neuro V ",
                        style = MaterialTheme.typography.displaySmall,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = userPrompt,
                        onValueChange = { userPrompt = it },
                        label = { Text("Ask something...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (userPrompt.isNotBlank()) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    executePrompt(userPrompt, context) {
                                        command = it
                                        fullMessage =
                                            if (it?.action == "open_app") "Opening App: ${it.app}" else "Running Command"
                                        userPrompt = ""
                                    }

                                    animatedMessage = ""
                                    fullMessage.forEach { char ->
                                        animatedMessage += char
                                        delay(25)
                                    }

                                    command?.let {
                                        ActionRunner(it, context).execute { action, output ->
                                            Log.d("ActionRunner", "Action: $action, Output: $output")
                                            onActionCompleted()
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text("Send")
                    }

                    Text(
                        text = animatedMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    )

                    Button(onClick = {
                        isListening = !isListening
                    }, modifier = Modifier.fillMaxWidth()) {
                        val transition = updateTransition(targetState = isListening, label = "ListeningTransition")

                        val scale by transition.animateFloat(
                            transitionSpec = { tween(durationMillis = 500) },
                            label = "ScaleAnimation"
                        ) { if (it) 1.1f else 1f }

                        val alpha by transition.animateFloat(
                            transitionSpec = { tween(durationMillis = 500) },
                            label = "AlphaAnimation"
                        ) { if (it) 1f else 0.5f }

                        Text(
                            text = if (isListening) "Stop Listening" else "Listen",
                            modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale, alpha = alpha),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
