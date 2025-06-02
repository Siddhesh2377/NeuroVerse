// Plugin.kt
package com.dark.plugin_api.info

import android.content.Context
import android.view.View

interface Plugin {
    val context: Context
    fun getName(): String
    fun run()
}
