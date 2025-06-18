package com.dark.neuroverse.compose.screens.setup.plugins

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dark.neuroverse.compose.components.CheckBX
import com.dark.neuroverse.compose.components.RichText
import com.dark.neuroverse.data.backend.fetchAllPlugins
import com.dark.neuroverse.data.models.PluginLink


@Composable
fun InstallPluginsScreen(paddingValues: PaddingValues, onNext: (List<PluginLink>) -> Unit) {
    val pluginList = remember { mutableStateListOf<PluginLink>() }
    val pluginCheckedMap = remember { mutableStateMapOf<PluginLink, Boolean>() }

    fetchAllPlugins { result ->
        pluginList.clear()
        pluginList.addAll(result)
        // Initialize map for all plugins
        result.forEach { plugin ->
            if (!pluginCheckedMap.contains(plugin)) {
                pluginCheckedMap[plugin] = false
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 34.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Plugins",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.displayMedium,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Select What You Want \n & What Not...",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Light
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(pluginList) { plugin ->
                        PluginCard(
                            title = plugin.name,
                            description = plugin.description,
                            checked = pluginCheckedMap[plugin] == true,
                            onCheckedChange = { isChecked ->
                                pluginCheckedMap[plugin] = isChecked
                            }
                        )
                    }
                }
            }
        }

        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(
                    "Neuro V",
                    modifier = Modifier.padding(horizontal = 24.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )

                Button(onClick = {
                    val downloadPluginsList = pluginCheckedMap.filter { it.value }.keys.toList()
                    Log.d("Install Plugin", "Plugins List $downloadPluginsList")
                    onNext(downloadPluginsList)
                }, enabled = true, colors = ButtonDefaults.buttonColors()) {
                    Text("Install & Proceed", fontFamily = FontFamily.Serif)
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PluginCard(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Serif,
                )

                CheckBX(
                    checked = checked,
                    onCheckStateChange = onCheckedChange
                )
            }

            RichText(
                description,
                style = MaterialTheme.typography.titleMediumEmphasized,
            )
        }
    }
}
