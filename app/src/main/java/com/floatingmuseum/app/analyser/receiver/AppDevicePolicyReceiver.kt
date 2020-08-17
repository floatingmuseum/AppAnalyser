package com.floatingmuseum.app.analyser.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Created by Floatingmuseum on 2020/7/10.
 */
class AppDevicePolicyReceiver : DeviceAdminReceiver() {

    companion object {
        private const val TAG = "AppDevicePolicyReceiver"

        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context.applicationContext, AppDevicePolicyReceiver::class.java)
        }
    }

    override fun onProfileProvisioningComplete(
        context: Context,
        intent: Intent
    ) {
        super.onProfileProvisioningComplete(context, intent)
        Log.d(TAG, "onProfileProvisioningComplete")
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.d(TAG, "onEnabled")
    }

    override fun onDisableRequested(
        context: Context,
        intent: Intent
    ): CharSequence? {
        Log.d(TAG, "onDisableRequested")
        return super.onDisableRequested(context, intent)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.d(TAG, "onDisabled")
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d(TAG, "onReceive")
    }
}