package com.dark.neuroverse.neurov.mcp.ai

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.dark.ai_manager.ai.api_calls.AiRouter
import com.dark.ai_manager.ai.local.Neuron
import com.dark.ai_manager.ai.types.NeuronVariant
import com.dark.plugin_runtime.database.installed_plugin_db.PluginInstalledDatabase
import com.dark.plugin_runtime.engine.PluginManager
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
    suspend fun process(prompt: String): ViewGroup? {
        return suspendCancellableCoroutine { cont ->

            val toolCallInput = JSONObject().apply {
                put("query", prompt)
                put("plugins_list", JSONArray().apply {
                    pluginDescriptions.forEach { (name, desc) ->
                        put(JSONObject().apply {
                            put("name", name)
                            put("description", desc)
                        })
                    }
                })
            }.toString()

            scope.launch {
                val response = Neuron.generateResponseBlocking(toolCallInput)
                    .substringBefore("<|im_end|>") // Clean trailing tokens if present
                    .trim()

                Log.d("PluginRouter", "AI Tool Response: $response")

                // Optionally parse the JSON if you want structured access
                try {
                    val json = JSONObject(response)
                    val pluginName = json.getString("plugin")
                    val reason = json.getString("reason")
                    Log.d("PluginRouter", "Selected plugin: $pluginName\nReason: $reason")
                } catch (e: Exception) {
                    Log.e("PluginRouter", "Invalid JSON: $response", e)
                }
            }





//
//            AiRouter.processRequest(
//                AiRouter.submitStructuredRequest(
//                    JSONObject().apply {
//                        put("messages", JSONArray().apply {
//                            put(JSONObject().apply {
//                                put("role", "system")
//                                put("content", "$pluginAiInstruction $pluginListText".trimIndent())
//                            })
//                            put(JSONObject().apply {
//                                put("role", "user")
//                                put("content", prompt)
//                            })
//                        })
//                    }
//                )
//            ) { code, response ->
//                when (code) {
//                    0 -> {
//                        Log.e("PluginRouter", "Error: AI Response: $response")
//                        cont.resume(null, null)
//                    }
//                    1 -> {
//                        Log.d("PluginRouter", "AI Response: $response")
//                        val data = phraseContent(response)
//                        if (data.code != 0) {
//                            PluginManager.runPlugin(data.pluginName.toString()) { pluginInstance ->
//
//                                scope.launch {
//                                    Neuron.generateResponse(pluginInstance.submitAiRequest(prompt))
//                                }
//                                AiRouter.processRequest(requestBody) { code2, response2 ->
//                                    val view = pluginInstance.onAiResponse(JSONObject(response2))
//                                    Log.d("PluginRouter", "Plugin view created.")
//                                    cont.resume(view, null)
//                                }
//                            }
//                        } else {
//                            cont.resume(null, null)
//                        }
//                    }
//                }
//            }
        }
    }


    data class PluginRouterData(
        val code: Int,
        val pluginName: String?,
        val reason: String
    )
}