package io.shubham0204.smollm

import io.shubham0204.smollm.SmolLM.InferenceParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

object SmollHelper {
    private var smollLM: SmolLM? = null

    suspend fun init(modelPath: String, contextLength: Long = 512L) {
        val file = File(modelPath)
        require(file.exists()) { "Model file does not exist at path: $modelPath" }

        smollLM?.close()
        smollLM = SmolLM()

        try {
            smollLM?.load(
                modelPath,
                InferenceParams(contextSize = contextLength)
            )
            smollLM?.addSystemPrompt(
                """
    You are an AI assistant designed to generate only valid JSON commands for Android app automation.
    Based on the user's natural language input, respond with a single JSON object using one of the following formats:

    {"action": "open_app", "data": "App Name"}
    {"action": "check_if_exists", "data": "App Name"}
    {"action": "list_installed_apps", "data": ""}

    Strict Rules:
    - Respond only with a JSON object, no explanation, no extra text.
    - Select the correct action type and populate "data" appropriately.
    - Do not include nulls, comments, or placeholders.

    Your output should always be a single clean JSON object matching one of the formats above.
    """.trimIndent()
            )

        } catch (e: Exception) {
            e.printStackTrace()
            throw e // or show error to user
        }
    }

    fun generateStream(input: String): Flow<String> = flow {
        smollLM?.addUserMessage(input)
        val outputFlow = smollLM?.getResponseAsFlow(input) ?: flow {}
        smollLM?.addAssistantMessage("") // You'll append to this later

        var fullResponse = ""
        outputFlow.collect { piece ->
            fullResponse += piece
            emit(fullResponse) // emit partial updates
        }

        smollLM?.addAssistantMessage(fullResponse) // Save final output
    }

    fun close() {
        smollLM?.close()
        smollLM = null
    }
}
