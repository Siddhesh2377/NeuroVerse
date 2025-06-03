package com.dark.plugin_runtime.engine

import android.content.Context
import com.dark.plugin_runtime.PluginLoader
import com.dark.plugin_runtime.engine.model.PluginInfo
import kotlinx.coroutines.DelicateCoroutinesApi

class PluginRunner(private val context: Context) {
    var pluginLoader: PluginLoader = PluginLoader(context)
    var runningPlugins: MutableList<PluginInfo> = mutableListOf()

    init {
        pluginLoader.extractPlugin("plugin")
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun runPlugin() {
        val plugin = pluginLoader.loadPlugin("plugin") {
            runningPlugins.add(it)
        }
        plugin.run()
    }

}