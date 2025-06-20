package com.dark.ai_manager.ai.local

import com.dark.ai_manager.ai.types.NeuronVariant
import io.shubham0204.smollm.SmolLM
import io.shubham0204.smollm.SmolLM.InferenceParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

object Neuron {

    data class Variant(
        val job: Job,
        val variant: NeuronVariant,
        val instance: SmolLM
    )


    private var activeVariant: NeuronVariant? = null
    private val modelInstances = ConcurrentHashMap<String, Variant>()
    private val nvScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun init(onLoaded: (() -> Unit)? = null) {
        val defaultModel = NeuronVariant.NVRouter
        loadModel(defaultModel, onLoaded = onLoaded)
    }

    fun loadModel(
        variant: NeuronVariant,
        contextLength: Long = 1212L,
        forceReload: Boolean = false,
        onLoaded: (() -> Unit)? = null // Optional callback
    ) {
        val file = File(variant.path)
        require(file.exists()) { "Model file does not exist at path: \${variant.path}" }

        // If already loaded, skip unless forceReload is true
        if (!forceReload && modelInstances.containsKey(variant.modelName)) {
            activeVariant = variant
            onLoaded?.invoke()
            return
        }

        // Unload existing model if needed
        unloadActiveModel()
        val model = SmolLM()

        val job = nvScope.launch {
            try {
                model.load(
                    variant.path,
                    InferenceParams(
                        contextSize = contextLength,
                        useMmap = true,
                        useMlock = true,
                        numThreads = Runtime.getRuntime().availableProcessors() - 2 // optimal for mobile
                    )
                )

                model.addSystemPrompt(variant.systemPrompt)

                withContext(Dispatchers.Main) {
                    onLoaded?.invoke()
                }

            } catch (e: Exception) {
                model.close()
                e.printStackTrace()
                throw e
            }
        }

        modelInstances[variant.modelName] = Variant(job, variant, model)
        activeVariant = variant
    }


    suspend fun generateResponse(input: String): String {
        val variant = activeVariant ?: error("No active variant selected.")
        val modelEntry = modelInstances[variant.modelName] ?: error("Model not loaded.")

        modelEntry.job.join() // 🔥 Wait for the model to finish loading before using it

        val model = modelEntry.instance
        model.addUserMessage(input)

        val outputFlow = model.getResponseAsFlow(input)
        val fullResponse = StringBuilder()

        outputFlow.collect { piece ->
            fullResponse.append(piece)
        }

        val responseStr = fullResponse.toString()
        model.addAssistantMessage(responseStr)

        return responseStr
    }

    fun unloadActiveModel() {
        activeVariant?.let {
            modelInstances[it.modelName]?.instance?.close()
            modelInstances.remove(it.modelName)
        }
        activeVariant = null
    }

    fun unloadAllModels() {
        modelInstances.forEach { (_, model) -> model.instance.close() }
        modelInstances.clear()
        activeVariant = null
    }

    fun listLoadedModels(): List<String> = modelInstances.keys.toList()
}