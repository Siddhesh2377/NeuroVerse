package com.dark.neuroverse.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import com.dark.ai_manager.ai.api_calls.AiRouter
import com.dark.neuroverse.BuildConfig
import com.dark.neuroverse.compose.screens.HomeScreen
import com.dark.neuroverse.neurov.mcp.ai.PluginRouter
import com.dark.neuroverse.ui.theme.NeuroVerseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PluginRouter.init(applicationContext)
        AiRouter.initAiRouter(BuildConfig.API_KEY)
        enableEdgeToEdge()
        setContent {
            NeuroVerseTheme {
                Scaffold {
                    HomeScreen(it)
                }
            }
        }
    }
}




