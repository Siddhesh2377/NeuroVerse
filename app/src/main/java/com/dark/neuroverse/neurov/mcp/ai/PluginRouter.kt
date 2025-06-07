package com.dark.neuroverse.neurov.mcp.ai

import android.content.Context
import android.util.Log
import com.dark.ai_manager.ai.api_calls.AiRouter
import com.dark.plugin_api.info.Plugin
import com.dark.plugin_runtime.PluginManager
import com.dark.plugin_runtime.database.installed_plugin_db.PluginInstalledDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.json.JSONObject

object PluginRouter {
    private lateinit var db: PluginInstalledDatabase
    private lateinit var pluginDescriptions: MutableList<Pair<String, String>>
    private lateinit var pluginManager: PluginManager

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.IO)

    fun init(context: Context) {
        db = PluginInstalledDatabase.getInstance(context)
        pluginManager = PluginManager(context.applicationContext)
        scope.launch {
            refreshDescriptionList()
        }
    }

    suspend fun refreshDescriptionList() {
        val list = getDb().pluginDao().getAllPlugins()
        pluginDescriptions = list.map { Pair(it.pluginName, it.pluginDescription) }.toMutableList()
    }


    fun getDb(): PluginInstalledDatabase {
        check(::db.isInitialized) { "PluginRouter not initialized. Call PluginRouter.init(context) first!" }
        return db
    }

    private fun phraseContent(content: Any): PluginRouterData {
        val contentData = when (content) {
            is String -> JSONObject(content)
            else -> {
                Log.e("PluginRouter", "Unexpected content: $content")
                return PluginRouterData(0, null, "Unexpected response format.")
            }
        }

        val code = contentData.optInt("code")
        val pluginName = contentData.optString("plugin_name")
        val reason = contentData.optString("message")

        Log.d("PluginRouter", "AI Response: $content")


        return PluginRouterData(code, pluginName, reason)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun process(
        prompt: String,
    ): Plugin? {

        var plugin: Plugin? = null

        val pluginListText = buildString {
            if (!::pluginDescriptions.isInitialized || pluginDescriptions.isEmpty()) {
                append("No plugins available.\n")
            } else {
                pluginDescriptions.forEachIndexed { index, (name, description) ->
                    append("${index + 1}. $name: $description\n")
                }
            }
        }


        AiRouter.processRequest(
            AiRouter.submitStructuredRequest(
                JSONObject().apply {
                    put("messages", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "system")
                            put("content", "$pluginAiInstruction $pluginListText".trimIndent())
                        })
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", prompt)
                        })
                    })
//
//                    // Structured output format
//                    put("response_format", JSONObject().apply {
//                        put("type", "json_schema")
//                        put("json_schema", pluginAiSchema)
//                    })
                })
        ) { code, response ->
            when (code) {
                0 -> {
                    Log.e("PluginRouter", "Error: AI Response: $response")
                }

                1 -> {
                    Log.d("PluginRouter", "AI Response: $response")

                    val data = phraseContent(response)

                    if (data.code != 0) {
                        pluginManager.runPlugin(data.pluginName.toString()) { it ->
                            plugin = it
                            Log.d("PluginRouter", "Plugin: ${it.getComposableScreen()}")
//                            val requestBody = AiRouter.submitStructuredRequest(it.submitAiRequest(prompt))
//                            AiRouter.processRequest(requestBody) { code, response ->
//                                it.onAiResponse(JSONObject(response))
//                            }
                        }
                    }

                }
            }
        }

        return plugin
    }

    data class PluginRouterData(
        val code: Int,
        val pluginName: String?,
        val reason: String
    )
}