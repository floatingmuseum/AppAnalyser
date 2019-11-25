package com.floatingmuseum.app.analyser.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Floatingmuseum on 2019-10-05.
 */

private const val DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss"

fun formatTime(time: Long, format: String = DEFAULT_FORMAT): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(Date(time))
}