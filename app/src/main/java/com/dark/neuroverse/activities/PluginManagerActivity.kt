package com.dark.neuroverse.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import com.dark.neuroverse.compose.screens.plugins.PluginScreen
import com.dark.neuroverse.ui.theme.NeuroVerseTheme

class PluginManagerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeuroVerseTheme {
                Scaffold {
                    PluginScreen(it)
                }
            }
        }
    }
}