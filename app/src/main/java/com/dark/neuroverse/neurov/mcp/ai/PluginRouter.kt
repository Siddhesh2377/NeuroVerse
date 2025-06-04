package com.dark.neuroverse.neurov.mcp.ai

import android.content.Context
import android.util.Log
import com.dark.neuroverse.BuildConfig
import com.dark.plugin_runtime.database.installed_plugin_db.PluginInstalledDatabase
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

object PluginRouter {
    private lateinit var db: PluginInstalledDatabase
    private lateinit var pluginDescriptions: MutableList<Pair<String, String>>

    fun init(context: Context) {
        db = PluginInstalledDatabase.getInstance(context)
        refreshDescriptionList()
    }

    fun refreshDescriptionList() {
        pluginDescriptions = mutableListOf()
        CoroutineScope(Dispatchers.IO).launch {
            val list = getDb().pluginDao().getAllPlugins()
            for (item in list) {
                pluginDescriptions.add(Pair(item.pluginName, item.pluginDescription))
            }
        }
    }

    fun getDb(): PluginInstalledDatabase {
        check(::db.isInitialized) { "PluginRouter not initialized. Call PluginRouter.init(context) first!" }
        return db
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun process(
        prompt: String,
        onResult: (String) -> Unit
    ): String = suspendCancellableCoroutine { cont ->
        val jsonBody = JSONObject().apply {
            put("model", "mistralai/mistral-7b-instruct")

            // stream removed
            // put("stream", true)  ← Removed

            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", """
                        You're a plugin router. Always return a JSON like {\"code\": int, \"plugin_name\": string|null, \"reason\": string} based on user's request and available plugins.
                You will be given a list of plugin names and descriptions.
                Match the user's request to one of the plugins.
                If a plugin matches, respond with code=1, plugin_name, and a helpful message.
                If none match, respond with code=0 and plugin_name=null.
                Only return a JSON object in the specified format.
            """.trimIndent())
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })

            // Structured output format
            put("response_format", JSONObject().apply {
                put("type", "json_schema")
                put("json_schema", JSONObject().apply {
                    put("type", "object")
                    put("properties", JSONObject().apply {
                        put("code", JSONObject().apply {
                            put("type", "integer")
                            put("description", "1 if plugin matched, 0 otherwise")
                        })
                        put("plugin_name", JSONObject().apply {
                            put("type", "string")
                            put("description", "Name of the matched plugin, or null if no match")
                        })
                        put("message", JSONObject().apply {
                            put("type", "string")
                            put("description", "Explanation of the routing decision")
                        })
                    })
                    put("required", JSONArray().apply {
                        put("code")
                        put("plugin_name")
                        put("message")
                    })
                })
            })
        }.toString()


        val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

        val request = okhttp3.Request.Builder()
            .url("https://openrouter.ai/api/v1/chat/completions")
            .addHeader("Authorization", "Bearer ${BuildConfig.API_KEY}")
            .addHeader("HTTP-Referer", "https://github.com/darkengine/NeuroVerse")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                cont.resume("Exception: ${e.message}", null)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    cont.resume("Error: ${response.code}", null)
                    return
                }

                val bodyString = response.body?.string() ?: ""
                Log.d("PluginRouter", "Response: $bodyString")

                val json = JSONObject(bodyString)
                val choices = json.getJSONArray("choices")
                val message = choices.getJSONObject(0).getJSONObject("message")
                val content = message.getString("content")
                val contentData = JSONObject(content) // content is from message.content
                val code = contentData.optInt("code")
                val pluginName = contentData.optString("plugin_name")
                val reason = contentData.optString("reason")

                Log.d("PluginRouter", "AI Response: $content")

                cont.resume(reason, null)
            }
        })
    }


//
//    interface OpenRouterApi {
//        @POST("v1/chat/completions")
//        fun chat(
//            @Body body: RequestBody,
//            @Header("Authorization") auth: String,
//            @Header("HTTP-Referer") referer: String,
//            @Header("Content-Type") contentType: String = "application/json"
//        ): Call<ResponseBody>
//    }

}