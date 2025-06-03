package com.dark.neuroverse.neurov.mcp.ai

import com.dark.neuroverse.BuildConfig
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun process(): String = suspendCancellableCoroutine { cont ->

    val retrofit = Retrofit.Builder()
        .baseUrl("https://openrouter.ai/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient())
        .build()

    val api = retrofit.create(OpenRouterApi::class.java)


    val jsonBody = JSONObject().apply {
        put("model", "google/gemma-3n-e4b-it:free")
        put("prompt", "fullPrompt")
        put("max_tokens", 512)
    }.toString()

    val body: RequestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = api.chat(
                body,
                auth = "Bearer ${BuildConfig.API_KEY}",
                referer = "com.dark.neuroverse"
            ).execute()

            val text = if (response.isSuccessful) {
                val json = JsonParser.parseString(response.body()?.string()).asJsonObject
                json.getAsJsonArray("choices")[0].asJsonObject.get("text").asString
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                "Error: $errorMsg"
            }

            cont.resume(text, null)
        } catch (e: Exception) {
            cont.resume("Exception: ${e.message}", null)
        }
    }
}


interface OpenRouterApi {
    @POST("v1/chat/completions")
    fun chat(
        @Body body: RequestBody,
        @Header("Authorization") auth: String,
        @Header("HTTP-Referer") referer: String,
        @Header("Content-Type") contentType: String = "application/json"
    ): Call<ResponseBody>
}


