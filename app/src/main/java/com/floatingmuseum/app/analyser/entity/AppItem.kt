package com.floatingmuseum.app.analyser.entity

import android.graphics.drawable.Drawable

/**
 * Created by Floatingmuseum on 2019-10-03.
 */
data class AppItem(
    val name: String,
    val pkg: String,
    val icon: Drawable?,
    val verName: String?,
    val isSystem: Boolean,
    var keyWord: String = ""
)