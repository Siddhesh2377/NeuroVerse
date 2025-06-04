package com.dark.plugin_runtime.engine

import android.util.Log
import com.dark.plugin_api.info.Plugin
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

object PluginExecutionManager {

    private val pluginScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val runningPlugins = ConcurrentHashMap<String, RunningPlugin>()

    data class RunningPlugin(
        val name: String,
        val job: Job,
        val instance: Plugin
    )

    fun launchPlugin(plugin: Plugin) {
        if (runningPlugins.containsKey(plugin.getName())) {
            Log.w("PluginManager", "⚠️ Plugin already running: ${plugin.getName()}")
            return
        }

        val job = pluginScope.launch {
            try {
                plugin.onStart()
                Log.i("PluginManager", "✅ Plugin started: ${plugin.getName()}")
            } catch (e: Exception) {
                Log.e("PluginManager", "❌ Error in plugin ${plugin.getName()}", e)
            }
        }

        runningPlugins[plugin.getName()] = RunningPlugin(plugin.getName(), job, plugin)
    }

    fun stopPlugin(name: String) {
        val running = runningPlugins[name] ?: return
        try {
            running.instance.onStop()
            running.job.cancel()
            Log.i("PluginManager", "🛑 Plugin stopped: $name")
        } catch (e: Exception) {
            Log.e("PluginManager", "❌ Failed to stop plugin: $name", e)
        }
        runningPlugins.remove(name)
    }

    fun stopAll() {
        runningPlugins.values.forEach {
            try {
                it.instance.onStop()
                it.job.cancel()
            } catch (_: Exception) { }
        }
        runningPlugins.clear()
        Log.i("PluginManager", "🧹 All plugins stopped.")
    }

    fun isPluginRunning(name: String): Boolean = runningPlugins.containsKey(name)

    fun listRunningPlugins(): List<String> = runningPlugins.keys().toList()
}
