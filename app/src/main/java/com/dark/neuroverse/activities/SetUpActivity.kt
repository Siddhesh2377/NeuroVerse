package com.dark.neuroverse.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import com.dark.neuroverse.compose.screens.setup.SetUpScreen
import com.dark.neuroverse.ui.theme.NeuroVerseTheme
import com.dark.neuroverse.utils.UserPrefs

class SetUpActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = this

            enableEdgeToEdge()
            NeuroVerseTheme {
                Scaffold(containerColor = MaterialTheme.colorScheme.surface) {
                    SetUpScreen(it)
                }
            }
        }
    }
}