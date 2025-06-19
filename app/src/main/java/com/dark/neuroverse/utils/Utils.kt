package com.dark.neuroverse.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.VibrationEffect
import android.os.VibratorManager
import android.provider.Settings

fun vibrate(context: Context) {
    val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

    val vibrationEffect = VibrationEffect.createOneShot(70, VibrationEffect.DEFAULT_AMPLITUDE)

    vm.defaultVibrator.vibrate(vibrationEffect)
}

fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
