package com.dark.plugin_api.info.view.config

import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.unit.Dp


val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics

val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        displayMetrics
    ).toInt()

val Int.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        displayMetrics
    )

//For ViewGroup
fun ViewGroup.wrapContentSize(): ViewGroup.LayoutParams {
    return ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
}

fun ViewGroup.fillMaxSize(): ViewGroup.LayoutParams {
    return ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
}

fun ViewGroup.fillMaxHeight(): ViewGroup.LayoutParams {
    val width = this.layoutParams?.width ?: ViewGroup.LayoutParams.MATCH_PARENT
    return ViewGroup.LayoutParams(
        width,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
}

fun ViewGroup.fillMaxWidth(): ViewGroup.LayoutParams {
    val height = this.layoutParams?.width ?: ViewGroup.LayoutParams.MATCH_PARENT
    return ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        height
    )
}


fun ViewGroup.height(h: Int): ViewGroup.LayoutParams {
    val width = this.layoutParams?.width ?: ViewGroup.LayoutParams.MATCH_PARENT
    return ViewGroup.LayoutParams(width, h)
}

fun ViewGroup.width(w: Int): ViewGroup.LayoutParams {
    val height = this.layoutParams?.width ?: ViewGroup.LayoutParams.MATCH_PARENT
    return ViewGroup.LayoutParams(w, height)
}

fun ViewGroup.size(w: Int, h: Int): ViewGroup.LayoutParams {
    return ViewGroup.LayoutParams(w, h)
}

fun ViewGroup.size(size: Int): ViewGroup.LayoutParams {
    return ViewGroup.LayoutParams(size, size)
}


//For View

fun View.wrapContentSize(): ViewGroup.LayoutParams {
    return ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
}

fun View.fillMaxSize(): ViewGroup.LayoutParams {
    return ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
}

fun View.fillMaxHeight(): ViewGroup.LayoutParams {
    val width = this.layoutParams?.width ?: ViewGroup.LayoutParams.MATCH_PARENT
    return ViewGroup.LayoutParams(
        width,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
}

fun View.fillMaxWidth(): ViewGroup.LayoutParams {
    val height = this.layoutParams?.width ?: ViewGroup.LayoutParams.MATCH_PARENT
    return ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        height
    )
}


fun View.height(h: Int): ViewGroup.LayoutParams {
    val width = this.layoutParams?.width ?: ViewGroup.LayoutParams.MATCH_PARENT
    return ViewGroup.LayoutParams(width, h)
}

fun View.width(w: Int): ViewGroup.LayoutParams {
    val height = this.layoutParams?.width ?: ViewGroup.LayoutParams.MATCH_PARENT
    return ViewGroup.LayoutParams(w, height)
}

fun View.size(w: Int, h: Int): ViewGroup.LayoutParams {
    return ViewGroup.LayoutParams(w, h)
}

fun View.size(size: Int): ViewGroup.LayoutParams {
    return ViewGroup.LayoutParams(size, size)
}