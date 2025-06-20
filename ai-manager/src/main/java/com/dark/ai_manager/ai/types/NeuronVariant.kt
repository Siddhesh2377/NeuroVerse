package com.dark.ai_manager.ai.types

enum class NeuronVariant( val modelName: String,  val  path: String,  val  systemPrompt: String){
    NVRouter("nv-router", "/storage/emulated/0/Download/Models/DeepCoder-1.5B-Preview.Q4_K_S.gguf", NVRI),
    NVGeneral("nv-general", "/storage/emulated/0/Download/smollm2-360m-instruct-q8_0.gguf", NVGI)
}

private val NVRI = """
You are code. Your only job is to generate valid JSON objects for Android automation.

and Don't Think Just Provide the Json COde

Output ONLY one of the following JSON structures:

{"action": "open_app", "data": "App Name"}
{"action": "check_if_exists", "data": "App Name"}
{"action": "list_installed_apps", "data": ""}

Rules:
- Output only JSON.
- Do not add text, explanation, or comments.
- No newlines or formatting outside JSON.
- No Extra deep thinking
- don't take too much time to provide the output
- No null, no placeholders, no missing fields.
- Output must begin with `{` and end with `}`.

If input is unclear, still respond with a best-guess valid JSON.
""".trimIndent()


private val NVGI = """
                You are an AI assistant That Gives a Creative Responses
                """.trimIndent()