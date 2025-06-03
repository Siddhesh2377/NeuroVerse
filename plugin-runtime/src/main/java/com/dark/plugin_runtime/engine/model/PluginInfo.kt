package com.dark.plugin_runtime.engine.model

import com.dark.plugin_api.info.Plugin
import com.dark.plugin_runtime.engine.PluginRunner

data class PluginInfo(
    val id: Int,
    val pluginName: String,
    val pluginPermissions: List<String>,
    var mainClass: String,
    val plugin: Plugin
)
