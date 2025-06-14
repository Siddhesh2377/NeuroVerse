package com.dark.neuroverse.compose.screens.temp

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dark.neuroverse.R
import com.dark.neuroverse.compose.components.RichText
import com.dark.neuroverse.ui.theme.White

@Composable
fun MainScreen(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 34.dp, vertical = 24.dp),
    ) {
        Header()

        MainCards()
    }
}

@Composable
fun Header() {
    val infiniteTransition = rememberInfiniteTransition()

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                "Hey \nSiddhesh..!",
                style = MaterialTheme.typography.displaySmall,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.weight(1f))

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
                        .rotate(rotation)
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RichText(
                "Browse Your **Protected-Data**\n" +
                        "Here.....",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Light
            )

            Icon(painterResource(R.drawable.shield), "protected data")
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MainCards() {

    var axis1 by remember { mutableStateOf(30.dp) }
    var axis2 by remember { mutableStateOf(0.dp) }

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val columnHeight = screenWidthDp - 100.dp
    val cardSpace = 8.dp
    val (edge, center) = Pair(25.dp, 10.dp)
    val iconModifier = Modifier.size(64.dp)

    val animatedAxis1 by animateDpAsState(
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = 0,
            easing = FastOutSlowInEasing
        ),
        targetValue = axis1,
        label = "CardElevation"
    )

    val animatedAxis2 by animateDpAsState(
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = 0,
            easing = FastOutSlowInEasing
        ),
        targetValue = axis2,
        label = "CardElevation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(columnHeight),
        verticalArrangement = Arrangement.spacedBy(cardSpace)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(cardSpace)
        ) {
            AnimatedCards(
                onClick = {
                    axis1 = 0.dp
                    axis2 = 30.dp
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(edge, center, center, center),
                value = animatedAxis1
            ) {
                Icon(painterResource(R.drawable.activity), "", iconModifier)
                Text(
                    "Your Activity",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Light
                )
            }

            AnimatedCards(
                onClick = {
                    axis1 = 30.dp
                    axis2 = 0.dp
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(center, edge, center, center),
                value = animatedAxis2
            ) {
                Icon(painterResource(R.drawable.movies), "", iconModifier)
                Text(
                    "Fav Movies",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Light
                )
            }
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(cardSpace)
        ) {
            AnimatedCards(
                onClick = {
                    axis1 = 30.dp
                    axis2 = 0.dp
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(center, center, center, edge),
                value = animatedAxis2
            ) {
                Icon(painterResource(R.drawable.apps), "", iconModifier)
                Text(
                    "Fav Apps",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Light
                )
            }

            AnimatedCards(
                onClick = {
                    axis1 = 0.dp
                    axis2 = 30.dp
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(center, center, edge, center),
                value = animatedAxis1
            ) {
                Icon(painterResource(R.drawable.neurov), "", iconModifier)
                Text(
                    "Your Chat’s",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}


@Composable
fun AnimatedCards(
    onClick: () -> Unit,
    modifier: Modifier,
    shape: Shape,
    value: Dp,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Card(
        onClick = {
            onClick()
        },
        shape = shape,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = value)
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}
