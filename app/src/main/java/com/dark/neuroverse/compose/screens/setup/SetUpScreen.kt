package com.dark.neuroverse.compose.screens.setup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dark.neuroverse.compose.screens.setup.intro.IntroScreen
import com.dark.neuroverse.compose.screens.setup.permissions.PermissionScreen
import com.dark.neuroverse.compose.screens.setup.plugins.InstallPluginsScreen
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalAnimationApi::class)
@Composable
fun SetUpScreen(paddingValues: PaddingValues) {
    var showLoading by remember { mutableStateOf(false) }
    var closeLoadingScreen by remember { mutableStateOf(true) }
    val animDuration = 500

    LaunchedEffect(Unit) {
        delay(700)
        showLoading = true
        delay(2700)
        closeLoadingScreen = true
    }

    AnimatedContent(closeLoadingScreen, transitionSpec = {
        (fadeIn(animationSpec = tween(durationMillis = animDuration)) +
                scaleIn(animationSpec = tween(durationMillis = animDuration)))
            .togetherWith(
                fadeOut(animationSpec = tween(durationMillis = animDuration)) +
                        scaleOut(animationSpec = tween(durationMillis = animDuration))
            )
    }, label = "setup") {
        when (it) {
            true -> PermissionScreen(paddingValues)
            false -> IntroScreen(showLoading)
        }
    }
}