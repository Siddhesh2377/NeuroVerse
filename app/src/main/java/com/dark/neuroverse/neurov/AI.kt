package com.dark.neuroverse.neurov

import android.content.Context
import android.util.Log
import com.dark.neuroverse.neurov.mcp.tools.ProgramsTool
import com.google.gson.Gson
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

data class Command(
    val action: String,
    val app: String? = null,
    val recipient: String? = null,
    val input: String? = null
)


suspend fun executePrompt(input: String, context: Context, onResult: (Command?) -> Unit) {
    val outputText = sendMessage(context, input)

    val jsonStart = outputText.indexOf("{")
    val jsonEnd = outputText.lastIndexOf("}")
    val cleanJson = if (jsonStart != -1 && jsonEnd != -1) {
        outputText.substring(jsonStart, jsonEnd + 1)
    } else {
        outputText
    }

    Log.d("Gemini Output", cleanJson)

    try {
        val command = Gson().fromJson(cleanJson, Command::class.java)
        onResult(command)
    } catch (e: Exception) {
        Log.e("Gemini Error", "Parsing error: ${e.message}")
        onResult(null)
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
suspend fun sendMessage(context: Context, prompt: String): String =
    suspendCancellableCoroutine { cont ->
        val apps = ProgramsTool().listApps(context)
        val appNames = apps.joinToString(", ") { it.name }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://openrouter.ai/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient())
            .build()

        val api = retrofit.create(OpenRouterApi::class.java)

        val fullPrompt = """
        You are an AI assistant that converts user instructions into JSON.
        Always respond with JSON only. No extra decoration like ```json or any title – just the pure JSON.

        Schema:
        {
          "action": "open_app" | "send_message" | "search" | "play_music",
          "app": "<app_name>",
          "input": "<input_text>"
        }

        Here is the list of installed apps on device:
        [$appNames]

        Examples:
        - "Open YouTube please" → {"action": "open_app", "app": "YouTube"}
        - "Launch Google Chrome" → {"action": "open_app", "app": "Chrome"}
        - "Play some music" → {"action": "play_music", "app": "Spotify"}

        Now interpret this: $prompt
    """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("model", "google/gemma-3n-e4b-it:free")
            put("prompt", fullPrompt)
            put("max_tokens", 512)
        }.toString()

        val body: RequestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.chat(
                    body,
                    auth = "Bearer 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",
                    referer = "com.dark.op"
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


