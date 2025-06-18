package com.dark.neuroverse.compose.screens.home.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dark.neuroverse.R
import com.dark.neuroverse.compose.components.RichText


@Composable
fun ChatScreen(paddingValues: PaddingValues) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header()

        Body(Modifier.weight(1f))

        BottomBar()
    }
}

@Composable
fun Header() {
    var deleteButton by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                "NeuroV Chat",
                style = MaterialTheme.typography.displaySmall,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.weight(1f))

            if (deleteButton) {
                IconButton(
                    onClick = {

                    },
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        Icons.Outlined.Settings,
                        "settings",
                        modifier = Modifier
                            .size(26.dp)
                    )
                }
            }

        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RichText(
                "Privacy Focused **Offline AI**\n" +
                        "On Your Device",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Light
            )

            Icon(painterResource(R.drawable.shield), "protected data")
        }
    }
}

@Composable
fun Body(modifier: Modifier) {
    Card(
        modifier = modifier.padding(bottom = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

        }
    }
}

@Composable
fun BottomBar() {
    var text by remember { mutableStateOf("") }

    // Main container (mimics the Card with a pill shape)
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 34.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .padding(start = 16.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(min = 100.dp, max = 250.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (text.isEmpty()) {
                                Text(
                                    "Say Anything...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                IconButton(
                    onClick = { /* Mic action */ },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Mic"
                    )
                }

                IconButton(
                    onClick = { /* Audio action */ }, colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Audio"
                    )
                }
            }
        }
    }
}
