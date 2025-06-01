// Plugin.kt
package com.dark.plugin_api.info

import android.content.Context

interface Plugin {
    fun getName(): String
    fun run(context: Context)
}
