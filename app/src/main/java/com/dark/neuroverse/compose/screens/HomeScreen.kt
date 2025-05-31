package com.dark.neuroverse.compose.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import com.dark.neuroverse.ui.theme.LightBlack

@Composable
fun HomeScreen(paddingValues: PaddingValues) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ElevatedButton(
            onClick = {
                val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)
                context.startActivity(intent)

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