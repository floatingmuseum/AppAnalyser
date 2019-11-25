package com.floatingmuseum.app.analyser.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes

/**
 * Created by Floatingmuseum on 2019-11-14.
 */

private var toast: Toast? = null

private var mGravity = -1
private var mOffsetX = -1
private var mOffsetY = -1

private var mKeepGravity = false

fun setToastGravity(gravity: Int, xOffset: Int, yOffset: Int, keepGravity: Boolean = false) {
    mGravity = gravity
    mOffsetX = xOffset
    mOffsetY = yOffset
    mKeepGravity = keepGravity
}

fun toastShort(context: Context, @StringRes resId: Int) {
    show(context, context.getString(resId), Toast.LENGTH_SHORT)
}

fun toastShort(context: Context, content: CharSequence) {
    show(context, content, Toast.LENGTH_SHORT)
}

fun toastLong(context: Context, @StringRes resId: Int) {
    show(context, context.getString(resId), Toast.LENGTH_LONG)
}

fun toastLong(context: Context, content: CharSequence) {
    show(context, content, Toast.LENGTH_LONG)
}

fun toastCustom(context: Context, @LayoutRes layoutId: Int, duration: Int = Toast.LENGTH_SHORT) {
    val view = getView(context, layoutId)
    show(context, view, duration)
}

private fun getView(context: Context, layoutId: Int): View {
    val inflate = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    return inflate.inflate(layoutId, null)
}

private fun setGravity() {
    if (mGravity != -1 || mOffsetX != -1 || mOffsetY != -1) {
        toast?.setGravity(mGravity, mOffsetX, mOffsetY)
        if (!mKeepGravity) {
            mGravity = -1
            mOffsetX = -1
            mOffsetY = -1
        }
    }
}

private fun show(context: Context, view: View, duration: Int) {
    toast?.cancel()
    toast = Toast(context)
    toast?.duration = duration
    toast?.view = view
    setGravity()
    toast?.show()
}

private fun show(context: Context, content: CharSequence, duration: Int) {
    toast?.cancel()
    toast = Toast.makeText(context, content, duration)
    setGravity()
    toast?.show()
}
