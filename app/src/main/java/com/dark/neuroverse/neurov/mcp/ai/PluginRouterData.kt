package com.dark.neuroverse.neurov.mcp.ai

import org.json.JSONArray
import org.json.JSONObject

val pluginAiInstruction = """
    You're a plugin router.
You MUST respond ONLY with a valid JSON object like:
{"code": 1, "plugin_name": "ExamplePlugin", "message": "Reason text"}

Do not include any extra text before or after the JSON.

Match the user's request to one of the available plugins based on name and description. 
If no match, respond with {"code": 0, "plugin_name": null, "message": "Explanation of why no plugin matched"}

Below is the plugin list:
""".trimIndent()

val pluginAiSchema = JSONObject().apply {
    put("type", "object")
    put("properties", JSONObject().apply {
        put("code", JSONObject().apply {
            put("type", "integer")
            put("description", "1 if plugin matched, 0 otherwise")
        })
        put("plugin_name", JSONObject().apply {
            put("type", "string")
            put("description", "Name of the matched plugin, or null if no match")
        })
        put("message", JSONObject().apply {
            put("type", "string")
            put("description", "Explanation of the routing decision")
        })
    })
    put("required", JSONArray().apply {
        put("code")
        put("plugin_name")
        put("message")
    })
}