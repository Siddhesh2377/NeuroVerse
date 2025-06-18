package com.dark.neuroverse.compose.screens.setup.intro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun IntroScreen(showLoading: Boolean) {
    Box(
        Modifier
            .fillMaxSize()
    ) {
        Card(
            modifier = Modifier.align(Alignment.Center),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
        ) {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .padding(horizontal = 54.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Neuro V",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.displayMedium
                )

                Text(
                    "Command Your Phone. Shape Your Experience.",
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )

                AnimatedVisibility(showLoading) {
                    LoadingIndicator(Modifier.size(74.dp))
                }
            }
        }
    }
}