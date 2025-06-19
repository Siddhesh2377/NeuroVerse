package com.dark.neuroverse.compose.screens.temp

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dark.ai_manager.ai.local.Neuron
import com.dark.ai_manager.ai.local.STT
import com.dark.ai_manager.ai.local.TTS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NeuronDemoScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    var response by remember { mutableStateOf("") }
    var neuronLoaded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Initialize STT, TTS, and Neuron once
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).async {
            Neuron.init {
                neuronLoaded = true
            }
        }
    }

    // React to new response and generate audio
    LaunchedEffect(response) {
        if (response.isNotBlank()) {
            CoroutineScope(Dispatchers.IO).async {
                TTS.generate(context, response)
                snapshotFlow { TTS.isGenerating }
                    .filter { !it }
                    .first()

               // TTS.play(context)
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            isListening = !isListening

            if (isListening) {
                STT.start(context) { inputText ->
                        scope.launch {
                            val result =
                                if (neuronLoaded) Neuron.generateResponse(inputText) else ""
                            response = result
                            isListening = false
                            STT.stop()
                        }
                }
            } else {
                STT.stop()
            }
        }) {
            AnimatedContent(isListening, label = "Listening") { listening ->
                if (listening) {
                    LoadingIndicator(
                        color = LoadingIndicatorDefaults.containedIndicatorColor.copy(
                            Color.White.alpha,
                            Color.Red.alpha,
                            Color.Green.alpha,
                            Color.Blue.alpha
                        )
                    )
                } else {
                    Text("Start")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(response)
    }
}