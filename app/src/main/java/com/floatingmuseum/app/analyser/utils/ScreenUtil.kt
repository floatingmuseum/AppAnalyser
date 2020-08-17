package com.floatingmuseum.app.analyser.utils

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi

/**
 * Created by Floatingmuseum on 2019-12-09.
 */

private fun Context.getMetrics(isRealMetrics: Boolean): DisplayMetrics? {
    val wm = getSystemService(WINDOW_SERVICE) as? WindowManager
    val outMetrics = DisplayMetrics()
    wm?.defaultDisplay?.getMetrics(outMetrics)
    Log.d("ScreenUtils", "getMetrics()...normal:$outMetrics")
    wm?.defaultDisplay?.getRealMetrics(outMetrics)
    Log.d("ScreenUtils", "getMetrics()...real:$outMetrics")
    if (isRealMetrics) {
        wm?.defaultDisplay?.getRealMetrics(outMetrics)
    } else {
        wm?.defaultDisplay?.getMetrics(outMetrics)
    }
    return outMetrics
}

/**
 * DPI:dots per inch
 * get device density
 */
fun Context.getDPI() = getMetrics(true)?.densityDpi ?: -1

fun Context.getDPIText(): String {
    val dpi = getDPI()
    return when {
        dpi < 0 -> "unknown"
        dpi <= 120 -> "ldpi"
        dpi <= 240 -> "hdpi"
        dpi <= 320 -> "xhdpi"
        dpi <= 480 -> "xxhdpi"
        dpi <= 640 -> "xxxhdpi"
        else -> "unknown"
    }
}

/**
 * Pair.x is screen width
 * Pair.y is screen height
 */
fun Context.getRealScreenSizeInPx(): Pair<Int, Int>? {
    getMetrics(true)?.let {
        return Pair(it.widthPixels, it.heightPixels)
    }
    return null
}

fun Context.getUsableScreenSizeInPx(): Pair<Int, Int>? {
    getMetrics(false)?.let {
        return Pair(it.widthPixels, it.heightPixels)
    }
    return null
}

/**
 * Pair.first is screen width
 * Pair.second is screen height
 */
fun Context.getRealScreenSizeInDp(): Pair<Float, Float>? {
    var size: Pair<Float, Float>? = null
    val density = getScreenDensity()
    density?.let {
        val pxSize = getRealScreenSizeInPx()
        pxSize?.let {
            val dpWidth = it.first / density
            val dpHeight = it.second / density
            size = Pair(dpWidth, dpHeight)
        }
    }
    return size
}

fun Context.getUsableScreenSizeInDp(): Pair<Float, Float>? {
    var size: Pair<Float, Float>? = null
    val density = getScreenDensity()
    density?.let {
        val pxSize = getUsableScreenSizeInPx()
        pxSize?.let {
            val dpWidth = it.first / density
            val dpHeight = it.second / density
            size = Pair(dpWidth, dpHeight)
        }
    }
    return size
}

fun Context.getScreenDensity() = getMetrics(true)?.density

fun Context.getStatusBarHeightInPx(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else -1
}

fun Context.getStatusBarHeightInDp(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    var height = -1
    if (resourceId > 0) {
        getMetrics(true)?.density?.let {
            height = resources.getDimensionPixelSize(resourceId) / it.toInt()
        }
    }
    return height
}

fun Context.getNavBarHeightInPx(): Int {
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else -1
}

fun Context.getNavBarHeightInDp(): Int {
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    var height = -1
    if (resourceId > 0) {
        getMetrics(true)?.density?.let {
            height = resources.getDimensionPixelSize(resourceId) / it.toInt()
        }
    }
    return height
}

fun Context.getScreenOrientation() = resources.configuration.orientation