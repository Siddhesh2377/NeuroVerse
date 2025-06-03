package com.dark.plugin_runtime.database.installed_plugin_db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InstalledPluginModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pluginName: String,
    val pluginPermissions: List<String>,
    var mainClass: String,
    val pluginApi: String
)