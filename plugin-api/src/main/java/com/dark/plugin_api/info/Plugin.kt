package com.dark.plugin_api.info

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import org.json.JSONObject

/**
 * Base class for plugins used in the NeuroVerse plugin system.
 *
 * Plugin developers can extend this class and override its methods to define
 * custom behavior, AI request handling, and lifecycle callbacks.
 *
 * @property context Android context passed by the host app at runtime.
 */
open class Plugin(protected val context: Context)  {

    /**
     * Returns the name of the plugin.
     * Override this to provide a unique and human-readable plugin identifier.
     *
     * @return A string representing the plugin's name.
     */
    open fun getName(): String {
        return "none"
    }

    /**
     * Called when the plugin is initialized or started.
     * Override this method to perform any setup or registration logic.
     */
    open fun onStart() {
        // Plugin startup logic can go here.
    }

    /**
     * Called when the plugin wants to send a request to the AI system.
     * Override this method to generate a custom prompt or structured payload.
     * The No Need To Add model parameters, They are already added by Default
     *
     * @return A [JSONObject] containing the AI request payload.
     */
    open fun submitAiRequest(prompt: String): JSONObject {
        return JSONObject()
    }

    /**
     * Called when the AI system returns a response to the plugin's request.
     * Override this method to handle the result from the AI.
     *
     * @param response The AI-generated response as a [JSONObject].
     */
    open fun onAiResponse(response: JSONObject): ViewGroup {
        // Handle AI response here.
        return LinearLayout(context)
    }

    /**
     * Called when the plugin is stopped or unloaded.
     * Override this method to perform any cleanup, unregister listeners, etc.
     */
    open fun onStop() {
        // Plugin cleanup logic can go here.
    }
}
