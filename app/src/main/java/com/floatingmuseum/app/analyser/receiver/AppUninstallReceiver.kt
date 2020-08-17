package com.floatingmuseum.app.analyser.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.floatingmuseum.app.analyser.callback.AppUninstallCallback

/**
 * Created by Floatingmuseum on 2019-12-02.
 */
class AppUninstallReceiver(private val callback: AppUninstallCallback) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.data?.schemeSpecificPart?.let {
            Log.d("uninstall","$it uninstalled,update app list")
            callback.onAppUninstall(it)
        }
    }
}