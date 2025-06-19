package com.dark.neuroverse.utils

import android.content.Context
import android.os.VibrationEffect
import android.os.VibrationEffect.EFFECT_CLICK
import android.os.VibratorManager

fun vibrate(context: Context) {
    val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

    val vibrationEffect = VibrationEffect.createOneShot(70, VibrationEffect.DEFAULT_AMPLITUDE)

    vm.defaultVibrator.vibrate(vibrationEffect)
}
