package com.dark.neuroverse.neurov.mcp.tools

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

class ProgramsTool {

    fun listApps(context: Context): List<AppInfo> {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val apps =
            packageManager.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
        return apps.map { resolveInfo ->
            val appName = resolveInfo.loadLabel(packageManager).toString()
            val packageName = resolveInfo.activityInfo.packageName
            val icon = resolveInfo.loadIcon(packageManager)
            AppInfo(appName, packageName, icon)
        }
    }

    fun launchApp(context: Context, packageName: String, onError: (err: String) -> Unit) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            onError("App not found")
        }
    }


    data class AppInfo(
        val name: String,
        val packageName: String,
        val icon: Drawable
    )

}