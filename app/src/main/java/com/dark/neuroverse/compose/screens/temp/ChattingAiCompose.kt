package com.dark.neuroverse.compose.screens.temp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dark.neuroverse.ui.theme.NeuroVerseTheme
import io.shubham0204.smollm.SmollHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ChattingAiCompose(userInput: String, speaking: Boolean, onResponse: (String) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var response by remember { mutableStateOf("") }

    LaunchedEffect(speaking) {
        if (speaking) return@LaunchedEffect
        coroutineScope.launch(Dispatchers.IO) {

            SmollHelper.loadModel(
                "test",
                "/storage/emulated/0/Download/smollm2-360m-instruct-q8_0.gguf"
            )

            SmollHelper.generateStream(userInput).collect { partial ->
                withContext(Dispatchers.Main) {
                    response = partial
                }
            }
        }
    }

    NeuroVerseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(response)
                onResponse(response)
            }
        }
    }
}
