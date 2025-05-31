package com.dark.neuroverse.activities

import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dark.neuroverse.compose.screens.assistant.AssistantScreen
import com.dark.neuroverse.ui.theme.NeuroVerseTheme

class AssistantActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Fullscreen immersive overlay
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.setDimAmount(0.4f) // Optional background dimming
        @Suppress("DEPRECATION")
        window.setDecorFitsSystemWindows(false)

        val controller = window.insetsController
        if (controller != null) {
            controller.hide(
                WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
            )
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }


        setContent {
            NeuroVerseTheme {

            }
        }
    }

}

