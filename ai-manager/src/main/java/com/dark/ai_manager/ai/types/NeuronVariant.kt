package com.dark.ai_manager.ai.types

enum class NeuronVariant( val modelName: String,  val  path: String,  val  systemPrompt: String){
    NVRouter("nv-router", "/storage/emulated/0/Download/Kodify_Nano_q4_k_s.gguf", NVRI),
    NVGeneral("nv-general", "/storage/emulated/0/Download/smollm2-360m-instruct-q8_0.gguf", NVGI)
}

private val NVRI = """
You are a JSON bot.

Rules:
- Output only valid JSON.
- No text, comments, or newlines.
- Always start with `{` and end with `}`.
- No nulls or missing fields.
- Respond quickly.

Task: Match a user query to a plugin Discretion from The plugins_list

If matched:
{"code":1,"plugin_name":"<name>","message":"Plugin matched"}

Else:
{"code":0,"plugin_name":null,"message":"No plugin matched"}

""".trimIndent()


private val NVGI = """
                You are an AI assistant That Gives a Creative Responses
                """.trimIndent()