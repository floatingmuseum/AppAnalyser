package com.floatingmuseum.app.analyser.utils

import android.util.Pair
import java.math.BigDecimal

/**
 * Created by Floatingmuseum on 2019-12-24.
 */

fun scaleDecimal(num: Float, scale: Int, roundingMode: Int = BigDecimal.ROUND_DOWN) =
    num.toBigDecimal().setScale(scale, roundingMode).toFloat()

fun scaleDecimal(num: Double, scale: Int, roundingMode: Int = BigDecimal.ROUND_DOWN) =
    num.toBigDecimal().setScale(scale, roundingMode).toDouble()

fun minScale(a: Int, b: Int): Pair<Int, Int> {
    var tmp = a
    if (a > b) {
        tmp = b
    }
    for (i in tmp downTo 1) {
        if (a % i == 0 && b % i == 0) {
            return Pair(a / i, b / i)
        }
    }
    return Pair(-1, -1)
}
