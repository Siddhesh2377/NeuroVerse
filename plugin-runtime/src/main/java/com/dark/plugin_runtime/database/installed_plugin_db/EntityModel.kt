package com.dark.plugin_runtime.database.installed_plugin_db

import androidx.room.*

@Entity(
    tableName = "InstalledPluginModel",
    indices = [
        Index(value = ["pluginName"], unique = true)
    ]
)
data class InstalledPluginModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "pluginName")
    val pluginName: String,              // ← this is now “unique”

    @ColumnInfo(name = "pluginDescription")
    val pluginDescription: String,

    @ColumnInfo(name = "pluginPermissions")
    val pluginPermissions: List<String>,

    @ColumnInfo(name = "manifestFile")
    val manifestFile: String,

    @ColumnInfo(name = "mainClass")
    var mainClass: String,

    @ColumnInfo(name = "pluginApi")
    val pluginApi: String,

    @ColumnInfo(name = "pluginPath")
    val pluginPath: String,

    @ColumnInfo(name = "isEnabled")
    val isEnabled: Boolean = false
)
