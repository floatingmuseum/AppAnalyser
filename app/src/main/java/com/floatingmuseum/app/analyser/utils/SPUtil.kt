package com.floatingmuseum.app.analyser.utils

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager


/**
 * Created by Floatingmuseum on 2019-10-08.
 */
object SPUtil {
    private lateinit var context: Application

    private val sp by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun init(application: Application) {
        context = application
    }

    fun containsKey(name: String, key: String, mode: Int = Context.MODE_PRIVATE) =
        context.getSharedPreferences(name, mode).contains(key)

    fun putString(
        name: String,
        key: String,
        value: String?,
        useCommit: Boolean = false,
        mode: Int = Context.MODE_PRIVATE
    ) {
        context.getSharedPreferences(name, mode)
            .edit(useCommit) {
                putString(key, value)
            }
    }

    fun getString(
        name: String,
        key: String,
        defaultValue: String,
        mode: Int = Context.MODE_PRIVATE
    ): String? {
        return context.getSharedPreferences(name, mode)
            .getString(key, defaultValue)
    }

    fun putString(key: String, value: String?, useCommit: Boolean = false) {
        sp.edit(useCommit) {
            putString(key, value)
        }
    }

    fun getString(key: String, defaultValue: String): String? {
        return sp.getString(key, defaultValue)
    }

    fun putInt(key: String, value: Int?, useCommit: Boolean = false) {
        value?.let {
            sp.edit(useCommit) {
                putInt(key, it)
            }
        }
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return sp.getInt(key, defaultValue)
    }

    fun putInt(
        name: String,
        key: String,
        value: Int?,
        useCommit: Boolean = false,
        mode: Int = Context.MODE_PRIVATE
    ) {
        value?.let {
            context.getSharedPreferences(name, mode)
                .edit(useCommit) {
                    putInt(key, it)
                }
        }
    }

    fun getInt(
        name: String,
        key: String,
        defaultValue: Int,
        mode: Int = Context.MODE_PRIVATE
    ): Int {
        return context.getSharedPreferences(name, mode)
            .getInt(key, defaultValue)
    }

    fun putLong(
        name: String,
        key: String,
        value: Long?,
        useCommit: Boolean = false,
        mode: Int = Context.MODE_PRIVATE
    ) {
        value?.let {
            context.getSharedPreferences(name, mode)
                .edit(useCommit) {
                    putLong(key, it)
                }
        }
    }

    fun getLong(
        name: String,
        key: String,
        defaultValue: Long,
        mode: Int = Context.MODE_PRIVATE
    ): Long {
        return context.getSharedPreferences(name, mode)
            .getLong(key, defaultValue)
    }

    fun putLong(key: String, value: Long?, useCommit: Boolean = false) {
        value?.let {
            sp.edit(useCommit) {
                putLong(key, it)
            }
        }
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return sp.getLong(key, defaultValue)
    }

    fun putFloat(
        name: String,
        key: String,
        value: Float?,
        useCommit: Boolean = false,
        mode: Int = Context.MODE_PRIVATE
    ) {
        value?.let {
            context.getSharedPreferences(name, mode)
                .edit(useCommit) {
                    putFloat(key, it)
                }
        }
    }

    fun getFloat(
        name: String,
        key: String,
        defaultValue: Float,
        mode: Int = Context.MODE_PRIVATE
    ): Float {
        return context.getSharedPreferences(name, mode)
            .getFloat(key, defaultValue)
    }

    fun putFloat(key: String, value: Float?, useCommit: Boolean = false) {
        value?.let {
            sp.edit(useCommit) {
                putFloat(key, it)
            }
        }
    }

    fun getFloat(key: String, defaultValue: Float): Float {
        return sp.getFloat(key, defaultValue)
    }

    fun putBoolean(name: String, key: String, useCommit: Boolean = false, value: Boolean?) {
        value?.let {
            context.getSharedPreferences(name, Context.MODE_PRIVATE)
                .edit(useCommit) {
                    putBoolean(key, it)
                }
        }
    }

    fun getBoolean(
        name: String,
        key: String,
        defaultValue: Boolean,
        mode: Int = Context.MODE_PRIVATE
    ): Boolean {
        return context.getSharedPreferences(name, mode)
            .getBoolean(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean?, useCommit: Boolean = false) {
        value?.let {
            sp.edit(useCommit) {
                putBoolean(key, it)
            }
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sp.getBoolean(key, defaultValue)
    }

    fun getStringSet(
        name: String,
        key: String,
        defaultValue: Set<String>,
        mode: Int = Context.MODE_PRIVATE
    ): Set<String>? {
        return context.getSharedPreferences(name, mode)
            .getStringSet(key, defaultValue)
    }

    fun putStringSet(
        name: String,
        key: String,
        value: Set<String>?,
        useCommit: Boolean = false,
        mode: Int = Context.MODE_PRIVATE
    ) {
        value?.let {
            context.getSharedPreferences(name, mode)
                .edit(useCommit) {
                    putStringSet(key, it)
                }
        }
    }

    fun remove(
        name: String,
        key: String,
        useCommit: Boolean = false,
        mode: Int = Context.MODE_PRIVATE
    ) {
        context.getSharedPreferences(name, mode)
            .edit(useCommit) {
                remove(key)
            }
    }

    fun remove(key: String, useCommit: Boolean = false) {
        sp.edit(useCommit) {
            remove(key)
        }
    }
}