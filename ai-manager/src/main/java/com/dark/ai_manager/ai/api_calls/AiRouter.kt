package com.dark.ai_manager.ai.api_calls

import android.util.Log
import com.dark.ai_manager.BuildConfig
import com.dark.ai_manager.ai.types.Models
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject

/**
 * AiRouter handles communication with the OpenRouter AI API.
 * It sends structured prompt requests (JSON Schema) and returns results via callback.
 */
object AiRouter {

    /**
     * API key used to authenticate with OpenRouter.ai
     * Must be initialized before making any requests.
     */
    var apiKey: String = BuildConfig.API_KEY

    /**
     * Prepares the full JSON request body with model information and input content.
     * Adds the `model` field and converts the provided JSONObject into a RequestBody.
     *
     * @param jsonObject The original payload containing `messages`, `response_format`, etc.
     * @return A ready-to-send OkHttp RequestBody.
     */
    fun submitStructuredRequest(jsonObject: JSONObject): RequestBody {
        jsonObject.put("model", Models.GEMMA327B.modelName)
        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
        return requestBody
    }

    /**
     * Builds the HTTP POST request with necessary headers and the given request body.
     *
     * @param requestBody The JSON body to be sent to OpenRouter.
     * @return An OkHttp Request ready to be executed.
     */
    private fun requestBuilder(requestBody: RequestBody): Request {
        return Request.Builder()
            .url("https://openrouter.ai/api/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("HTTP-Referer", "https://github.com/siddhesh2377/NeuroVerse")
            .addHeader("X-Title", "NeuroV: Automation App")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()
    }


    /**
     * Executes the AI request asynchronously using OkHttp.
     * Parses the response and invokes the callback with success or failure.
     *
     * @param requestBody The structured JSON request body created by [submitStructuredRequest].
     * @param onResponse A callback with:
     *  - `1` and valid content on success
     *  - `0` and error message on failure
     */
    fun processRequest(requestBody: RequestBody, onResponse: (Int, String) -> Unit) {
        val request = requestBuilder(requestBody)
        Log.d("AiRouter", "POST → ${request.url}")
        for ((name, value) in request.headers) {
            Log.d("AiRouter", "Header: $name → $value")
        }

        OkHttpClient().newCall(requestBuilder(requestBody)).enqueue(object : okhttp3.Callback {

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                onResponse(0, "Exception: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    onResponse(0, "Error: ${response.code}")
                    return
                }

                val bodyString = response.body?.string() ?: ""
                Log.d("PluginRouter", "Response: $bodyString")

                try {
                    val json = JSONObject(bodyString)
                    val choices = json.getJSONArray("choices")
                    val message = choices.getJSONObject(0).getJSONObject("message")
                    val content = message.get("content")
                    onResponse(1, content.toString())
                } catch (e: Exception) {
                    onResponse(0, "Parsing Error: ${e.message}")
                }
            }
        })
    }
}
