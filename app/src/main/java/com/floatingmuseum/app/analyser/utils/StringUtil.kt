package com.floatingmuseum.app.analyser.utils

/**
 * Created by Floatingmuseum on 2019-11-07.
 */

/**
 *
 */
fun separateString(
    source: List<CharSequence>,
    separator: String,
    shouldAppendAtEnd: Boolean = false
): String {
    val buffer = StringBuffer()
    for (charSequence in source) {
        buffer.append("$charSequence$separator")
    }
    if (!shouldAppendAtEnd) {
        buffer.deleteCharAt(buffer.lastIndex)
    }
    return buffer.toString()
}