package com.dark.plugin_runtime.model

import androidx.room.*

@Entity(
    tableName = "InstalledPluginModel",
    indices = [
        Index(value = ["pluginName"], unique = true)
    ]
)
data class PluginModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pluginName: String,              // ← this is now “unique”
    val pluginDescription: String,
    val pluginPermissions: List<String>,
    val manifestFile: String,
    var mainClass: String,
    val pluginApi: String,
    val pluginPath: String,
    val isEnabled: Boolean = false
)
