package com.dark.neuroverse.neurov.mcp.ai

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
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

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun process(prompt: String, onPluginLoaded: (String) -> Unit): ViewGroup? {
        return suspendCancellableCoroutine { cont ->
            val toolCallInput = JSONObject().apply {
                put("query", prompt)
                put("plugins", JSONArray().apply {
                    pluginDescriptions.forEach { (name, desc) ->
                        put(JSONObject().apply {
                            put("name", name)
                            put("description", desc)
                        })
                    }
                })
            }.toString()


            Log.e("PluginRouter", "Tool Call Input:\n$toolCallInput")  // ✅ correct logging


            scope.launch {
                val response = Neuron.generateResponseStreaming(toolCallInput)
                    .substringBefore("<|im_end|>") // Clean trailing tokens if present
                    .trim()

                Log.d("PluginRouter", "AI Tool Response: $response")

                // Optionally parse the JSON if you want structured access
                try {
                    val json = JSONObject(response)
                    val code = json.getInt("code")
                    val pluginName = json.getString("plugin_name")
                    val reason = json.getString("message")


                    when(code){
                        0 -> {

                        }
                        1 -> {
                            PluginManager.runPlugin(pluginName) { pluginInstance ->

                                scope.launch {
                                    val temp = Neuron.generateResponseStreaming(pluginInstance.submitAiRequest(prompt))
                                    onPluginLoaded(temp)
                                }

//
//                                AiRouter.processRequest(requestBody) { code2, response2 ->
//                                    val view = pluginInstance.onAiResponse(JSONObject(response2))
//                                    Log.d("PluginRouter", "Plugin view created.")
//                                    cont.resume(view) { cause, _, _ -> null?.let { it(cause) } }
//                                }
                            }
                        }
                    }




                    Log.d("PluginRouter", "AI Response: $response")
                    Log.d("PluginRouter", "Code: $code \nPlugin Name: $pluginName \nReason: $reason")
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
}