package com.dark.neuroverse.neurov

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.dark.neuroverse.neurov.mcp.tools.ProgramsTool
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking

@SuppressLint("SecretInSource")
object GeminiClient {
    val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "AIzaSyDW2y4GbYgpMtjh6kwJbi5Mmd-_KHH5SmU"
        )
    }
}

data class Command(
    val action: String,
    val app: String? = null,
    val recipient: String? = null,
    val input: String? = null
)


fun executePrompt(input: String, context: Context): Command? = runBlocking {
    val apps = ProgramsTool().listApps(context)
    val appNames = apps.joinToString(", ") { it.name }

    val dynamicChat = GeminiClient.generativeModel.startChat(
        history = listOf(
            content {
                text(
                    """
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

                    Based on the user's request, identify the most relevant app from the list above.

                    Examples:
                    - "Open YouTube please" → {"action": "open_app", "app": "YouTube"}
                    - "Launch Google Chrome" → {"action": "open_app", "app": "Chrome"}
                    - "Play some music" → {"action": "play_music", "app": "Spotify"}
                    
                    Now interpret this:
                    "$input"
                    """.trimIndent()
                )
            }
        )
    )

    val response = dynamicChat.sendMessage(input)
    val outputText = response.text ?: return@runBlocking null

    val jsonStart = outputText.indexOf("{")
    val jsonEnd = outputText.lastIndexOf("}")
    val cleanJson = if (jsonStart != -1 && jsonEnd != -1) {
        outputText.substring(jsonStart, jsonEnd + 1)
    } else {
        outputText
    }

    Log.d("Gemini Output", cleanJson)

    try {
        Gson().fromJson(cleanJson, Command::class.java)
    } catch (e: Exception) {
        Log.e("Gemini Error", "Parsing error: ${e.message}")
        null
    }
}

