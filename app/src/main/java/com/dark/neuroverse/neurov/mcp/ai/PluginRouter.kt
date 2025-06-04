package com.dark.neuroverse.neurov.mcp.ai

import android.content.Context
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
            put("stream", true)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "You are an AI assistant. Respond concisely and help the user interact with apps.")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
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

                val builder = StringBuilder()
                response.body?.charStream()?.forEachLine { line ->
                    if (line.startsWith("data: ")) {
                        val jsonLine = line.removePrefix("data: ").trim()
                        if (jsonLine == "[DONE]") return@forEachLine
                        try {
                            val delta = JsonParser.parseString(jsonLine)
                                .asJsonObject["choices"]?.asJsonArray?.get(0)?.asJsonObject
                                ?.get("delta")?.asJsonObject

                            val content = delta?.get("content")?.asString
                            if (content != null) {
                                builder.append(content)
                                onResult(builder.toString())
                            }
                        } catch (_: Exception) {}
                    }
                }
                cont.resume(builder.toString(), null)
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