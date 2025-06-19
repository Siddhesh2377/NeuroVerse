package com.dark.neuroverse.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.core.view.WindowCompat
import com.dark.neuroverse.neurov.mcp.ai.PluginRouter
import com.dark.neuroverse.ui.theme.NeuroVerseTheme
import com.dark.plugin_runtime.engine.PluginManager
import com.google.firebase.FirebaseApp
import com.dark.neuroverse.compose.screens.temp.NeuronDemoScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PluginRouter.init(applicationContext)
        PluginManager.init(applicationContext)
        FirebaseApp.initializeApp(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        enableEdgeToEdge()
        setContent {
            NeuroVerseTheme {
                Scaffold(containerColor = MaterialTheme.colorScheme.surface) { it ->

//                    NeuroVScreen(onClickOutside = {
//                        PluginManager.loadSTTPlugins()
//                    })

                    NeuronDemoScreen(it)

                       // ChatScreen(it)
                        //STTScreen(it)

                        // HomeScreen(it)
                        // runPluginInSandbox(this, "List Applications Plugin")

                }
            }
        }
    }

}



