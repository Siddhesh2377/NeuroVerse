package com.dark.neuroverse.compose.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Backpack
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dark.neuroverse.ui.theme.NeuroVerseTheme
import com.dark.neuroverse.ui.theme.SoftWhite
import com.dark.neuroverse.viewModel.PluginScreenViewModel
import com.dark.plugin_runtime.PluginManager
import com.dark.plugin_runtime.database.installed_plugin_db.InstalledPluginModel
import com.dark.plugin_runtime.database.installed_plugin_db.PluginInstalledDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalAnimationApi::class)
@Composable
fun PluginScreen(paddingValues: PaddingValues, viewModel: PluginScreenViewModel = viewModel()) {

    val context = LocalContext.current
    var isRefreshing by remember { mutableStateOf(false) }

    var db = remember { PluginInstalledDatabase.getInstance(context) }
    var isImportingPlugin by remember { mutableStateOf(false) }
    val plugins by viewModel.pluginsList.collectAsState()
    var pluginManager = PluginManager(context)


    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                isImportingPlugin = false

                // 🔥 Trigger plugin install
                pluginManager.installPlugin(it) { pluginData ->
                    Log.d("PluginInstall", "✅ Installed to ${pluginData.manifestFile}")
                    CoroutineScope(Dispatchers.IO).launch {
                        db.pluginDao().insertPlugin(pluginData)
                        viewModel.refreshPlugins(db)
                    }
                }
            }
        }
    )


    LaunchedEffect(isImportingPlugin) {
        if (isImportingPlugin) {
            filePickerLauncher.launch(arrayOf("application/zip"))
        }
    }

    LaunchedEffect(isRefreshing) {
        viewModel.refreshPlugins(db)
    }


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

        Box(Modifier.weight(1f)) {
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
                        PluginScreenMainContent(plugins, onPluginDeleted = { plugin ->
                            pluginManager.unInstallPlugin(plugin.pluginPath) { isDeleted ->
                                if (isDeleted) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        db.pluginDao().deletePlugin(plugin.id)
                                        viewModel.refreshPlugins(db)
                                    }
                                }
                            }
                        })
                    }
                }
            }
        }

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


}

@Composable
fun PluginScreenMainContent(
    plugins: List<InstalledPluginModel>,
    onPluginDeleted: (plugin: InstalledPluginModel) -> Unit
) {
    if (plugins.isEmpty()) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("No Plugins Here...")
        }
    } else {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(plugins) { plugin ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.onBackground
                        ),
                        onClick = { /* navigate to plugin details or perform action */ }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = plugin.pluginName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                IconButton(onClick = { onPluginDeleted(plugin) }) {
                                    Icon(
                                        imageVector = Icons.TwoTone.Delete,
                                        contentDescription = "Delete Plugin",
                                        tint = MaterialTheme.colorScheme.onError
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Permissions:",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = ">> ${plugin.pluginPermissions.joinToString(", ")}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Main Class:",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = ">> ${plugin.mainClass}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Plugin API:${plugin.pluginApi}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.background,
                                        shape = MaterialTheme.shapes.large
                                    )
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
