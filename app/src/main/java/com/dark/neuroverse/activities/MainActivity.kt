package com.dark.neuroverse.activities

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.dark.neuroverse.compose.screens.PluginScreen
import com.dark.neuroverse.ui.theme.NeuroVerseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeuroVerseTheme {
                val context = LocalContext.current
//                val pluginRunner = PluginRunner(context)
//                pluginRunner.runPlugin()

                Scaffold {
                    PluginScreen(it)
                }
            }


        }
    }
}




