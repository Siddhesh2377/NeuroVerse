package com.dark.plugin_runtime.model

import androidx.room.*
import com.dark.plugin_api.info.services.types.ServiceType
import java.io.File

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
    val autoStart: Boolean,
    val manifestFile: String,
    var mainClass: String,
    val pluginApi: String,
    val pluginPath: String,
    val isEnabled: Boolean = false
)

data class ServicePlugins(
    val pluginName: String,
    val serviceType: ServiceType,
    val serviceClass: String
)