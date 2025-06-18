package com.dark.neuroverse.data.models

data class PluginLink(
    val name: String = "",
    val description: String = "",
    val downloadUrl: String = "",
    val hasUpdate: Boolean = false,
    val apiVersion: String = "",
    val pluginVersion: String = ""
)