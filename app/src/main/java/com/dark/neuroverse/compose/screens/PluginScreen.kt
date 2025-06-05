package com.dark.neuroverse.compose.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Backpack
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dark.neuroverse.compose.components.RichText
import com.dark.neuroverse.compose.components.SingleChoiceSegmentedButton
import com.dark.neuroverse.data.backend.downloadAndInstall
import com.dark.neuroverse.data.backend.fetchAllPlugins
import com.dark.neuroverse.data.models.PluginLink
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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf("Installed") }

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
                    // inside your callback from pluginManager.installPlugin(…)
                    CoroutineScope(Dispatchers.IO).launch {
                        val rowId = db.pluginDao().insertPlugin(pluginData)
                        if (rowId == -1L) {
                            Log.d(
                                "PluginInstall",
                                "⚠️ Plugin “${pluginData.pluginName}” already installed; skipping."
                            )
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "⚠️ Plugin “${pluginData.pluginName}” already installed; skipping.",
                                    actionLabel = "Dismiss",
                                    duration = SnackbarDuration.Long
                                )
                            }

                        } else {
                            Log.d(
                                "PluginInstall",
                                "✅ Inserted “${pluginData.pluginName}” (rowId=$rowId)."
                            )
                        }
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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Plugin Screen",
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium
                )

                Crossfade(currentScreen) {
                    Text(
                        "$it Plugins",
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = FontFamily.Serif
                    )
                }

            }


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
                        AnimatedContent(currentScreen) {
                            when (it) {
                                "Installed" -> InstalledPluginScreen(
                                    plugins,
                                    onPluginDeleted = { plugin ->
                                        pluginManager.unInstallPlugin(plugin.pluginPath) { isDeleted ->
                                            if (isDeleted) {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    db.pluginDao().deletePlugin(plugin.id)
                                                    viewModel.refreshPlugins(db)
                                                }
                                            }
                                        }
                                    })

                                "Market" -> {
                                    val pluginList = remember { mutableStateListOf<PluginLink>() }

                                    // Fetch only once (on first composition)
                                    LaunchedEffect(Unit) {
                                        fetchAllPlugins { result ->
                                            pluginList.clear()
                                            pluginList.addAll(result)
                                        }
                                    }

                                    MarketPluginScreen(
                                        plugins = pluginList,
                                        db = db
                                    ) { selectedPlugin ->
                                        downloadAndInstall(
                                            plugin = selectedPlugin,
                                            context = context,
                                            db = db,
                                            onSuccess = {
                                                viewModel.refreshPlugins(db)
                                                Toast.makeText(
                                                    context,
                                                    "Plugin Installed",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                Log.d(
                                                    "PluginMarket",
                                                    "✅ Installed ${selectedPlugin.name}"
                                                )
                                            },
                                            onFailure = {
                                                Log.w("PluginMarket", it)
                                                Toast.makeText(
                                                    context,
                                                    it,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, end = 26.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            SingleChoiceSegmentedButton(modifier = Modifier.padding(start = 26.dp)) { index, label ->
                currentScreen = label
            }

            Spacer(Modifier.weight(1f))

            ExtendedFloatingActionButton(
                onClick = { isImportingPlugin = !isImportingPlugin },
                expanded = !isImportingPlugin,
                modifier = Modifier.height(53.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Backpack,
                        contentDescription = "Add Plugin",
                    )
                },
                text = {
                    Text(
                        text = "Import Plugin",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                },
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

}

@Composable
fun MarketPluginScreen(
    plugins: List<PluginLink>,
    db: PluginInstalledDatabase,
    onDownloadClick: suspend (PluginLink) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(plugins) { plugin ->
            MarketPluginCard(plugin = plugin, db = db, onDownloadClick = onDownloadClick)
        }
    }
}


@Composable
fun MarketPluginCard(
    plugin: PluginLink,
    db: PluginInstalledDatabase,
    onDownloadClick: suspend (PluginLink) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isInstalled by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }

    // Check if already installed
    LaunchedEffect(plugin.name) {
        isInstalled = db.pluginDao().getPluginByName(plugin.name) != null
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
    ) {
        Column {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = plugin.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Description:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
            RichText(
                text = plugin.description,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Plugin API:${plugin.apiVersion}",
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

                Text(
                    text = "Plugin Version:${plugin.pluginVersion}",
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

            if (plugin.hasUpdate) {
                Text("⚠️ Update Available", color = Color.Yellow, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (!isInstalled && !isDownloading) {
                        isDownloading = true
                        scope.launch {
                            Toast
                                .makeText(
                                    context,
                                    "⬇️ Downloading ${plugin.name}",
                                    Toast.LENGTH_SHORT
                                )
                                .show()

                            onDownloadClick(plugin) // Call your actual download logic
                            isDownloading = false
                            isInstalled = true
                        }
                    }
                },
                enabled = !isInstalled && !isDownloading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    disabledContainerColor = Color.LightGray
                )
            ) {
                if (isDownloading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.Black
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Downloading...", color = Color.Black)
                } else if (isInstalled) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.Black)
                    Spacer(Modifier.width(8.dp))
                    Text("Installed", color = Color.Black)
                } else {
                    Icon(Icons.Default.Download, contentDescription = null, tint = Color.Black)
                    Spacer(Modifier.width(8.dp))
                    Text("Download", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun InstalledPluginScreen(
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp)
        ) {
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

                            Row {
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

                                Spacer(Modifier.weight(1f))
                                val context = LocalContext.current
                                var isChecked = remember { mutableStateOf(plugin.isEnabled) }

                                Switch(
                                    isChecked.value, onCheckedChange = {
                                        isChecked.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            PluginInstalledDatabase.getInstance(context)
                                                .pluginDao()
                                                .updatePluginEnabled(plugin.id, it)
                                        }
                                    },
                                    thumbContent = {
                                        Icon(
                                            imageVector = if (isChecked.value) Icons.Filled.Check else Icons.Outlined.Clear,
                                            contentDescription = "Refresh",
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }, colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.onBackground,
                                        uncheckedThumbColor = MaterialTheme.colorScheme.onBackground,
                                        checkedTrackColor = MaterialTheme.colorScheme.background,
                                        uncheckedTrackColor = MaterialTheme.colorScheme.background,
                                        uncheckedBorderColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
