package com.dark.ai_manager.ai.types

enum class NeuronVariant( val modelName: String,  val  path: String,  val  systemPrompt: String){
    NVRouter("nv-router", "/storage/emulated/0/Download/qwen2.5-coder-3b-instruct-q5_0.gguf", NVRI),
    NVGeneral("nv-general", "/storage/emulated/0/Download/smollm2-360m-instruct-q8_0.gguf", NVGI)
}

private val NVRI = """
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

private val NVGI = """
                You are an AI assistant That Gives a Creative Responses
                """.trimIndent()