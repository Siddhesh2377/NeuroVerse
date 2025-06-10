package com.dark.neuroverse.compose.screens

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dark.neuroverse.R
import com.dark.neuroverse.ui.theme.NeuroVerseTheme
import com.dark.plugin_runtime.engine.PluginManager
import com.dark.plugin_runtime.model.PluginModel


private val cardColor = Color(0xFFEFEFEF)

@Composable
fun NeuroVScreen(onClickOutside: () -> Unit) {
    // Whole screen, with extra rounding for that 'container' look
    NeuroVerseTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClickOutside() }
                .padding(horizontal = 12.dp)
                .padding(bottom = 34.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HeaderCard()
                Body()
                BottomBar()
            }
        }
    }
}

@Composable
fun HeaderCard() {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp, 12.dp, 6.dp, 6.dp))
            .background(cardColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Neuro V",
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(16.dp)
        )

        ElevatedButton(
            onClick = {
                //Open Settings Activity
            },
            modifier = Modifier.padding(end = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text(
                "Settings", style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily.Serif,
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                painter = painterResource(R.drawable.settings), contentDescription = "Settings"
            )
        }
    }
}

@Composable
fun Body() {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionCard(
            modifier = Modifier.weight(1f),
            icon = painterResource(R.drawable.typing),
            title = "Write To AI",
            desc = "Feel to be Private..? Try Typing Your Task To AI...."
        )
        QuickActionCard(
            modifier = Modifier.weight(1f),
            icon = painterResource(R.drawable.mic),
            title = "Speak..!",
            desc = "No Need To Type, Just Click And Let the Magic Happen"
        )
        QuickActionCard(
            modifier = Modifier.weight(1f),
            icon = painterResource(R.drawable.brain), // swap for your AGU icon
            title = "AGU",
            desc = "Let the AI understand the surrounding & make decisions",
            true
        )
    }
}

@Composable
fun BottomBar() {
    val pluginList = remember { mutableStateOf<List<PluginModel>>(emptyList()) }

    PluginManager.updateInstalledPlugins()

    LaunchedEffect(Unit) {
        PluginManager.InstalledPlugins.collect { plugins ->
            Log.d("MyService", "Loaded services: $plugins")
            pluginList.value = plugins
        }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp, 6.dp, 12.dp, 12.dp))
            .background(cardColor), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Plugins Actions",
            modifier = Modifier.padding(horizontal = 24.dp),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
        )

        VerticalDivider(modifier = Modifier.height(50.dp), thickness = 2.dp)

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 10.dp)
        ) {
            items(pluginList.value) {
                BottomNavButton(it.pluginName, selected = false)
            }
        }
    }
}

@Composable
fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String,
    desc: String,
    isCheckable: Boolean = false
) {
    var checked by remember { mutableStateOf(false) }

    val animColor = animateColorAsState(
        if (checked) Color(0xFF0FB100) else Color.Black, animationSpec = tween(
            durationMillis = 500, easing = FastOutSlowInEasing
        )
    )

    val highlightColor = if (isCheckable) animColor.value else Color.Black

    Card(
        modifier,
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable {
                        if (isCheckable) {
                            checked = !checked
                        } else {
                            Log.d("QuickActionCard", "Clicked")
                        }

                    },
            ) {
                Column(
                    modifier = Modifier.size(84.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = title,
                        tint = highlightColor,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = if (highlightColor != Color.Unspecified) highlightColor else Color.Black,
                            fontWeight = FontWeight.SemiBold
                        ),
                    )
                }
            }

            Text(
                desc,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Light,
                lineHeight = 14.sp,
                maxLines = 3,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp),
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun BottomNavButton(text: String, selected: Boolean) {
    Box(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) Color.Black else Color.White)
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(
            text,
            color = if (selected) Color.White else Color.Black,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 16.sp
        )
    }
}
