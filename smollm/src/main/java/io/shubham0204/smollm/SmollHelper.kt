package io.shubham0204.smollm

import android.content.Context
import io.shubham0204.smollm.SmolLM.InferenceParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.component1
import kotlin.collections.component2

object SmollHelper {
    private var activeModelName: String? = null
    private val modelInstances = ConcurrentHashMap<String, SmolLM>()

    suspend fun loadModel(
        name: String,
        modelPath: String,
        contextLength: Long = 1024L,
        forceReload: Boolean = false
    ) = withContext(Dispatchers.IO) {
        val file = File(modelPath)
        require(file.exists()) { "Model file does not exist at path: $modelPath" }

        // If already loaded, skip unless forceReload is true
        if (!forceReload && modelInstances.containsKey(name)) {
            activeModelName = name
            return@withContext
        }

        // Unload existing model if needed
        unloadActiveModel()

        val model = SmolLM()
        try {
            model.load(
                modelPath,
                InferenceParams(
                    contextSize = contextLength,
                    useMmap = true,
                    useMlock = false,
                    storeChats = false,
                    numThreads = Runtime.getRuntime().availableProcessors() / 2 // optimal for mobile
                )
            )

            model.addSystemPrompt(
                """
                You are an AI assistant designed to generate only valid JSON commands for Android app automation.
                Based on the user's natural language input, respond with a single JSON object using one of the following formats:

                {"action": "open_app", "data": "App Name"}
                {"action": "check_if_exists", "data": "App Name"}
                {"action": "list_installed_apps", "data": ""}

                {
                  "type": "object",
                  "properties": {
                    "action": {
                      "type": "string",
                      "enum": ["open_app", "check_if_exists", "list_installed_apps"]
                    },
                    "data": {
                      "type": "string"
                    }
                  },
                  "required": ["action", "data"]
                }

                Strict Rules:
                - Respond only with a JSON object, no explanation, no extra text.
                - Select the correct action type and populate "data" appropriately.
                - Do not include nulls, comments, or placeholders.
                """.trimIndent()
            )

            modelInstances[name] = model
            activeModelName = name

        } catch (e: Exception) {
            model.close()
            e.printStackTrace()
            throw e
        }
    }

    fun generateStream(input: String): Flow<String> = flow {
        val model = getActiveModelOrThrow()
        model.addUserMessage(input)
        val outputFlow = model.getResponseAsFlow(input)
        var fullResponse = ""
        outputFlow.collect { piece ->
            fullResponse += piece
            emit(fullResponse)
        }
        model.addAssistantMessage(fullResponse)
    }

    fun unloadActiveModel() {
        activeModelName?.let { name ->
            modelInstances[name]?.close()
            modelInstances.remove(name)
        }
        activeModelName = null
    }

    fun unloadAllModels() {
        modelInstances.forEach { (_, model) -> model.close() }
        modelInstances.clear()
        activeModelName = null
    }

    fun listLoadedModels(): List<String> = modelInstances.keys.toList()

    private fun getActiveModelOrThrow(): SmolLM {
        return modelInstances[activeModelName]
            ?: error("No active model loaded. Use loadModel() before inference.")
    }
}

