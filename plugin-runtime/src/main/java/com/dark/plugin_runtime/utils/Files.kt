package com.dark.plugin_runtime.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun queryFileName(uri: Uri, context: Context): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null) ?: return null
    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    cursor.moveToFirst()
    val name = if (nameIndex >= 0) cursor.getString(nameIndex) else null
    cursor.close()
    return name
}