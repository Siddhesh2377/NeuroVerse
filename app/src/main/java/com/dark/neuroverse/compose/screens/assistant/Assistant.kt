package com.dark.neuroverse.compose.screens.assistant

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.dark.neuroverse.neurov.mcp.voice.STT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun AssistantScreen(onClickOutside: () -> Unit) {
    val context = LocalContext.current
    var userPrompt by remember { mutableStateOf("") }
    var fullMessage by remember { mutableStateOf("Waiting for input...") }
    var animatedMessage by remember { mutableStateOf("Hello User I am Here To Help You navigate Your PHONE with ease") }
    var isListening by remember { mutableStateOf(false) }
    var command: Command? by remember { mutableStateOf(Command("open_app", "YouTube")) }
    var isRecognizerReady by remember { mutableStateOf(false) }

    val stt = remember { STT(context) }

    // Initialize recognizer once
    LaunchedEffect(Unit) {
        stt.initRecognizer { ready ->
            isRecognizerReady = ready
        }
    }

    // Start/Stop listening whenever isListening changes
    LaunchedEffect(isListening, isRecognizerReady) {
        if (isRecognizerReady) {
            if (isListening) {
                stt.startListening { textResult ->
                    Log.d("STT", "Recognized: $textResult")
                    userPrompt = textResult
                }
            } else {
                stt.stopListening()
            }
        }
    }


    Box(
        Modifier
            .fillMaxSize()
            .clickable { onClickOutside() }
    ) {
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
                // Show animated Gemini output
                Text(
                    text = "Neuro V ",
                    style = MaterialTheme.typography.displaySmall,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .fillMaxWidth()
                )

                // Prompt input
                OutlinedTextField(
                    value = userPrompt,
                    onValueChange = { userPrompt = it },
                    label = { Text("Ask something...") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Send button
                Button(
                    onClick = {
                        if (userPrompt.isNotBlank()) {
                            // Run Gemini and animate message
                            CoroutineScope(Dispatchers.IO).launch {
                                executePrompt(userPrompt, context) {
                                    command = it

                                    fullMessage =
                                        command?.let { "Action: ${if (it.action == "open_app") "Opening App : ${it.app}" else "Running Command"}" }
                                            .toString()

                                    userPrompt = ""
                                }
                                // Animate response
                                animatedMessage = ""
                                fullMessage.forEach { char ->
                                    animatedMessage += char
                                    kotlinx.coroutines.delay(25)
                                }

                                // Trigger action
                                command?.let {
                                    ActionRunner(it, context).execute { action, output ->
                                        Log.d(
                                            "ActionRunner",
                                            "Action: $action, Output: $output"
                                        )
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text("Send")
                }

                // Show animated Gemini output
                Text(
                    text = animatedMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )


                Button(onClick = {
                    if (isListening) stt.release()
                    isListening = !isListening
                }, modifier = Modifier.fillMaxWidth()) {
                    val transition =
                        updateTransition(targetState = isListening, label = "ListeningTransition")

                    val scale by transition.animateFloat(
                        transitionSpec = { tween(durationMillis = 500) },
                        label = "ScaleAnimation"
                    ) { state ->
                        if (state) 1.1f else 1f
                    }

                    val alpha by transition.animateFloat(
                        transitionSpec = { tween(durationMillis = 500) },
                        label = "AlphaAnimation"
                    ) { state ->
                        if (state) 1f else 0.5f
                    }

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
        }
    }
}


