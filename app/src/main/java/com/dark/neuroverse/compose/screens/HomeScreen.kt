package com.dark.neuroverse.compose.screens

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import com.dark.neuroverse.compose.components.SnackbarDemoScreen
import com.dark.neuroverse.ui.theme.LightBlack

@Composable
fun HomeScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current

    // Launcher for permission request
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)
                context.startActivity(intent)
            }
        }
    )

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ElevatedButton(
            onClick = {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            },
            colors = ButtonDefaults.buttonColors().copy(containerColor = LightBlack)
        ) {
            Text(
                "Setup NeuroV",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
