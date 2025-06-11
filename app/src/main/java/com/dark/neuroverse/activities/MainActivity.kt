package com.dark.neuroverse.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import com.dark.neuroverse.compose.screens.assistant.NeuroVScreen
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
        val db = Firebase.firestore

        enableEdgeToEdge()
        setContent {
            NeuroVerseTheme {
                Scaffold { _ ->
                    NeuroVScreen(onClickOutside = {

                    })
                   // HomeScreen(it)
                    // runPluginInSandbox(this, "List Applications Plugin")
                }
            }
        }
    }

}



