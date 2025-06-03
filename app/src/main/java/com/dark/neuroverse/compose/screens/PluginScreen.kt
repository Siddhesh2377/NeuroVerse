package com.dark.neuroverse.compose.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Backpack
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dark.plugin_runtime.database.installed_plugin_db.InstalledPluginModel
import com.dark.plugin_runtime.database.installed_plugin_db.PluginInstalledDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalAnimationApi::class)
@Composable
fun PluginScreen(paddingValues: PaddingValues) {

    val context = LocalContext.current
    var isRefreshing by remember { mutableStateOf(false) }
    var pluginsList by remember { mutableStateOf<List<InstalledPluginModel>>(emptyList()) }
    var db = remember { PluginInstalledDatabase.getInstance(context) }
    rememberCoroutineScope()
    var isImportingPlugin by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        pluginsList = db.pluginDao().getAllPlugins()
    }


//    PluginManager(LocalContext.current).installPlugin("/storage/emulated/0/Download/plugins/list_applications_plugin.zip") {
//
//    }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 28.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Plugin Screen",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.weight(1f))

            IconButton(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        isRefreshing = true
                        delay(3400)
                        isRefreshing = false
                    }
                }
            ) { Icon(Icons.Default.Refresh, contentDescription = "Refresh") }

        }

        Spacer(Modifier.weight(1f))

        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, end = 26.dp),
            horizontalArrangement = Arrangement.End
        ) {
            ExtendedFloatingActionButton(
                onClick = { isImportingPlugin = !isImportingPlugin },
                expanded = !isImportingPlugin,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Backpack,
                        contentDescription = "Add Plugin",
                        tint = if (!isImportingPlugin) Color.Black else Color.LightGray
                    )
                },
                text = {
                    Text(
                        text = "Import Plugin",
                        color = if (!isImportingPlugin) Color.Black else Color.LightGray
                    )
                },
                containerColor = if (isImportingPlugin) Color.Black else Color.LightGray
            )


        }
    }

    AnimatedContent(isRefreshing, transitionSpec = {
        (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
    }, label = "refresh") {
        when (it) {
            true -> {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LoadingIndicator(Modifier.size(100.dp))
                }
            }

            false -> {
                PluginScreenMainContent()
            }
        }
    }
}

@Composable
fun PluginScreenMainContent() {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("No Plugins Here...")
    }
}