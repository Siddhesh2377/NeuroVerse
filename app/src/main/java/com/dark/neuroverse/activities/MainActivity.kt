package com.dark.neuroverse.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.graphics.Color
import com.dark.neuroverse.compose.screens.assistant.NeuroVScreen
import com.dark.neuroverse.compose.screens.home.chat.ChatScreen
import com.dark.neuroverse.compose.screens.temp.MainScreen
import com.dark.neuroverse.compose.screens.temp.STTScreen
import com.dark.neuroverse.neurov.mcp.ai.PluginRouter
import com.dark.neuroverse.ui.theme.NeuroVerseTheme
import com.dark.plugin_runtime.engine.PluginManager
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PluginRouter.init(applicationContext)
        PluginManager.init(applicationContext)
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContent {
            NeuroVerseTheme {
                Scaffold(containerColor = MaterialTheme.colorScheme.surface) { it ->
//                    NeuroVScreen(onClickOutside = {
//                        PluginManager.loadSTTPlugins()
//                    })

                    ChatScreen(it)
                    //STTScreen(it)

                   // HomeScreen(it)
                    // runPluginInSandbox(this, "List Applications Plugin")
                }
            }
        }
    }

}



