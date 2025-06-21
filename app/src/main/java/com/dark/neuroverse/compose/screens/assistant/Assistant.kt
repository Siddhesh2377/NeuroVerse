package com.dark.neuroverse.compose.screens.assistant

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dark.neuroverse.compose.components.GlitchTypingText
import com.dark.neuroverse.neurov.mcp.ai.PluginRouter.process
import com.dark.neuroverse.ui.theme.NeuroVerseTheme
import com.dark.neuroverse.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun AssistantScreen(
    onClickOutside: () -> Unit,
    onActionCompleted: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var userPrompt by remember { mutableStateOf("Hey List the Android Apps") }
    var displayMessage by remember { mutableStateOf("Hello User, I am here to help you navigate your phone with ease") }
    var isProcessing by remember { mutableStateOf(false) }
    var showPluginView by remember { mutableStateOf(false) }
    var pluginView by remember { mutableStateOf<ViewGroup?>(null) }

    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            scope.launch {
                try {
                    val response = process(userPrompt) {}
                    pluginView = response
                    Log.e("Assistant Screen", "Router Response is >> $response")
                    showPluginView = true
                } catch (e: Exception) {
                    Log.e("AssistantScreen", "executePrompt failed: ${e.message}")
                } finally {
                    isProcessing = false
                    //onActionCompleted()
                }
            }
        }
    }

    AnimatedContent(
        targetState = showPluginView,
        transitionSpec = {
            (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
        },
        label = "Animated Content",
    ) { targetShowPluginView ->
        if (targetShowPluginView) {
            pluginView?.let { currentView ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = onClickOutside)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(24.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        AndroidView(
                            factory = { context ->
                                FrameLayout(context).apply {
                                    layoutParams = FrameLayout.LayoutParams(
                                        FrameLayout.LayoutParams.MATCH_PARENT,
                                        FrameLayout.LayoutParams.MATCH_PARENT
                                    )
                                    addView(currentView)
                                }
                            },
                            update = { frameLayout ->
                                frameLayout.removeAllViews()
                                frameLayout.addView(currentView)
                                frameLayout.invalidate()
                            }
                        )
                    }
                }
            } ?: run {
                Log.e("PluginManager", "Plugin view is null.")
                showPluginView = false
            }
        } else {
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
                                onValueChange = {
                                    userPrompt = it
                                },
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

                            GlitchTypingText(
                                finalText = displayMessage,
                                delayPerChar = 10L,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            )

                            ListenButton(
                                isProcessing = isProcessing,
                                onToggleListening = {
                                    if (!isProcessing) {
                                        isProcessing = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ListenButton(
    isProcessing: Boolean,
    onToggleListening: () -> Unit
) {
    Button(
        onClick = onToggleListening,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        AnimatedContent(
            targetState = isProcessing,
            transitionSpec = {
                (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
            },
            label = "Animated Button"
        ) { targetIsProcessing ->
            if (targetIsProcessing) {
                LoadingIndicator(color = White)
            } else {
                Text(
                    text = "Listen",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}