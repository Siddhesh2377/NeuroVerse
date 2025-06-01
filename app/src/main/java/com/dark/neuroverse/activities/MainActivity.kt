package com.dark.neuroverse.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composer
import androidx.compose.ui.platform.LocalContext
import com.dark.neuroverse.ui.theme.NeuroVerseTheme
import com.dark.plugin_runtime.PluginLoader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeuroVerseTheme {
                val context = LocalContext.current
                val pluginLoader = PluginLoader(context)
                pluginLoader.extractPlugin("plugin")

                val plugin = pluginLoader.loadPlugin("plugin")
                plugin.run(context)
            }
        }
    }
}