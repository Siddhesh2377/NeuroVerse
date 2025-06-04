package com.dark.plugin_api.info

import android.content.Context

interface Plugin {
    val context: Context
    fun getName(): String
    fun onStart()
    fun onStop()
}
