package com.floatingmuseum.app.analyser.utils

import android.app.AppOpsManager
import android.content.Context.APP_OPS_SERVICE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.annotation.RequiresApi
import com.floatingmuseum.app.analyser.App

/**
 * Created by Floatingmuseum on 2019-10-10.
 */

@RequiresApi(Build.VERSION_CODES.M)
fun checkUsageStats(): Boolean {
    val granted: Boolean
    val appOps = App.context.getSystemService(APP_OPS_SERVICE) as AppOpsManager
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            App.context.packageName
        )
    } else {
        appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            App.context.packageName
        )
    }
    granted = if (mode == AppOpsManager.MODE_DEFAULT) {
        App.context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PERMISSION_GRANTED
    } else {
        mode == AppOpsManager.MODE_ALLOWED
    }
    return granted
}

@RequiresApi(Build.VERSION_CODES.M)
fun hasPermission(permission: String) =
    App.context.checkSelfPermission(permission) == PERMISSION_GRANTED