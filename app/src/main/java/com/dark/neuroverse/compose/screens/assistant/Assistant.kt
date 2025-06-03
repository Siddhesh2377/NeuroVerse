// File: app/src/main/java/com/dark/neuroverse/compose/screens/assistant/Assistant.kt

package com.dark.neuroverse.compose.screens.assistant

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.dark.neuroverse.neurov.mcp.voice.GoogleSpeechRecognizer
import com.dark.neuroverse.ui.theme.NeuroVerseTheme
import com.dark.neuroverse.ui.theme.White
import kotlinx.coroutines.delay

/**
 * Top-level Composable screen for the assistant interface.
 */
@SuppressLint("MissingPermission")
@Composable
fun AssistantScreen(
    onClickOutside: () -> Unit,
    onActionCompleted: () -> Unit
) {
    val context = LocalContext.current

    var userPrompt by remember { mutableStateOf("") }
    var displayMessage by remember { mutableStateOf("Hello User, I am here to help you navigate your phone with ease") }
    var isListening by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    // Create recognizer once
    val recognizer = remember {
        GoogleSpeechRecognizer(context = context) { text ->
            userPrompt = text
            isProcessing = true
        }
    }

    // Dispose the recognizer if needed
    DisposableEffect(Unit) {
        onDispose {
            recognizer.stopListening()
            recognizer.destroy()
        }
    }

    LaunchedEffect(isListening) {
        if (isListening) {
            recognizer.startListening()
        } else {
            recognizer.stopListening()
        }
    }

    LaunchedEffect(userPrompt) {
        delay(1200)
        try {
            // Run the AI processing here. Set isProcessing = false when done.
            // For example:
            // val result = aiProcess(userPrompt)
            // displayMessage = result
        } catch (e: Exception) {
            Log.e("AssistantScreen", "executePrompt failed: ${e.message}")
        } finally {
            isProcessing = false
        }
    }

    // UI
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
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ask something…") },
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
                        isProcessing = isProcessing,
                        onToggleListening = { listening ->
                            if (!isProcessing) {
                                isListening = listening
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ListenButton(
    isListening: Boolean,
    isProcessing: Boolean,
    onToggleListening: (Boolean) -> Unit
) {
    // Animate scale and alpha based on listening state
    val transition = updateTransition(targetState = isListening, label = "ListeningTransition")

    val scale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) },
        label = "ScaleAnimation"
    ) { state -> if (state) 1.1f else 1f }

    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) },
        label = "AlphaAnimation"
    ) { state -> if (state) 1f else 0.7f }

    Button(
        onClick = { onToggleListening(!isListening) },
        enabled = !isProcessing,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale, alpha = alpha),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        if (isProcessing) {
            LoadingIndicator(color = White)
        } else {
            Text(
                text = if (isListening) "Stop Listening" else "Listen",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
