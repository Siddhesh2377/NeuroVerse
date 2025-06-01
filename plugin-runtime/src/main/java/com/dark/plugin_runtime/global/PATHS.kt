package com.dark.plugin_runtime.global

import android.content.Context
import java.io.File

fun getPluginDir(context: Context): File {
    return File(context.filesDir, "neurov/plugins")
}
