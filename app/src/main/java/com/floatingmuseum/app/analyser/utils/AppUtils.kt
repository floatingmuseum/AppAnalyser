package com.floatingmuseum.app.analyser.utils

import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.*
import android.content.pm.PackageManager.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import com.floatingmuseum.app.analyser.App
import com.floatingmuseum.app.analyser.entity.AppItem
import java.io.File
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
import android.os.Build
import androidx.annotation.RequiresApi
import android.content.pm.PackageInfo


/**
 * Created by Floatingmuseum on 2019-10-03.
 */

private const val TAG = "AppUtils"

private val sdkVerNameMap by lazy {
    mapOf(
        7 to "2.1",
        8 to "2.2",
        9 to "2.3",
        10 to "2.3.3",
        11 to "3.0",
        12 to "3.1",
        13 to "3.2",
        14 to "4.0",
        15 to "4.0.3",
        16 to "4.1",
        17 to "4.2",
        18 to "4.3",
        19 to "4.4",
        20 to "4.4W",
        21 to "5.0",
        22 to "5.1",
        23 to "6.0",
        24 to "7.0",
        25 to "7.1.1",
        26 to "8.0",
        27 to "8.1",
        28 to "9.0",
        29 to "10.0"
    )
}

private val sdkVerNicknameMap by lazy {
    mapOf(
        7 to "Eclair",
        8 to "Froyo",
        9 to "Gingerbread",
        10 to "Gingerbread",
        11 to "Honeycomb",
        12 to "Honeycomb",
        13 to "Honeycomb",
        14 to "IceCreamSandwich",
        15 to "IceCreamSandwich",
        16 to "Jelly Bean",
        17 to "Jelly Bean",
        18 to "Jelly Bean",
        19 to "Kitkat",
        20 to "Kitkat Wear",
        21 to "Lollipop",
        22 to "Lollipop",
        23 to "Marshmallow",
        24 to "Nougat",
        25 to "Nougat",
        26 to "Oreo",
        27 to "Oreo",
        28 to "Pie",
        29 to "Q"
    )
}

const val TYPE_APP_SYSTEM = 0
const val TYPE_APP_THIRD = 1

fun getAllAppMap(): Map<Int, MutableList<AppItem>> {
    val packageManager = App.context.packageManager
    val packageInfoList = packageManager.getInstalledPackages(0)
    val thirdList = mutableListOf<AppItem>()
    val systemList = mutableListOf<AppItem>()
    for (info in packageInfoList) {
        val name = info.applicationInfo.loadLabel(packageManager).toString()
        val packageName = info.packageName
        val icon = info.applicationInfo.loadIcon(packageManager)
        val verName = info.versionName ?: ""
        val isSystem = (info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) > 0
        Log.d(
            "报错",
            "name:$name...pkg:$packageName...icon:$icon...verName:$verName...isSystem:$isSystem"
        )
        val appItem = AppItem(
            name,
            packageName,
            icon,
            verName,
            isSystem
        )
        if (isSystem) {
            systemList.add(appItem)
        } else {
            thirdList.add(appItem)
        }
    }
    val map = hashMapOf<Int, MutableList<AppItem>>()
    map[TYPE_APP_SYSTEM] = systemList
    map[TYPE_APP_THIRD] = thirdList
    return map
}

/**
 * Acquire all applications
 */
fun getAllApp(): MutableList<AppItem> {
    val packageManager = App.context.packageManager
    val packageInfoList = packageManager.getInstalledPackages(0)
    val appList = mutableListOf<AppItem>()
    for (info in packageInfoList) {
        val name = info.applicationInfo.loadLabel(packageManager).toString()
        val packageName = info.packageName
        val icon = info.applicationInfo.loadIcon(packageManager)
        val verName = info.versionName
        val isSystem = (info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) > 0
        val appItem = AppItem(
            name,
            packageName,
            icon,
            verName,
            isSystem
        )
        if (isSystem) {
            appList.add(appItem)
        } else {
            appList.add(0, appItem)
        }
    }
    return appList
}

fun getInstallPackageInfo():List<PackageInfo>{
    val results = ArrayList<PackageInfo>()
    val packageManager = App.context.packageManager
    try {
        val packageInfoList = packageManager.getInstalledPackages(0)
        results.addAll(packageInfoList)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return results
}

/**
 * Acquire all system applications
 */
fun getSystemApp(): MutableList<AppItem> {
    val appList = mutableListOf<AppItem>()
    for (item in getAllApp()) {
        if (item.isSystem) {
            appList.add(item)
        }
    }
    return appList
}

/**
 * Acquire all third party applications
 */
fun getThirdPartyApp(): MutableList<AppItem> {
    val appList = mutableListOf<AppItem>()
    for (item in getAllApp()) {
        if (!item.isSystem) {
            appList.add(item)
        }
    }
    return appList
}

fun getPackageInfo(pkg: String): PackageInfo? {
    var packageInfo: PackageInfo? = null
    try {
        val signInfoFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            GET_SIGNING_CERTIFICATES
        } else {
            GET_SIGNATURES
        }
        packageInfo = App.context.packageManager.getPackageInfo(
            pkg, GET_ACTIVITIES or GET_CONFIGURATIONS or GET_GIDS or GET_INSTRUMENTATION
                    or GET_INTENT_FILTERS or GET_META_DATA or GET_PERMISSIONS or GET_PROVIDERS
                    or GET_RECEIVERS or GET_SERVICES or GET_SHARED_LIBRARY_FILES
                    or GET_URI_PERMISSION_PATTERNS or signInfoFlag
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return packageInfo
}

fun getSdkVersion(intVer: Int): Triple<Int, String, String> {
    val verName = sdkVerNameMap[intVer] ?: ""
    val verNickname = sdkVerNicknameMap[intVer] ?: ""
    return Triple(intVer, verName, verNickname)
}

fun isAppInstalled(pkg: String?): Boolean {
    var isInstalled = false
    try {
        pkg?.let {
            App.context.packageManager.getApplicationInfo(pkg, GET_META_DATA)
            isInstalled = true
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return isInstalled
}

fun getLauncherIntent(pkg: String?): Intent? {
    var intent: Intent? = null
    pkg?.let {
        intent = App.context.packageManager.getLaunchIntentForPackage(it)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent?.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
    }
    return intent
}

fun getAppSettingsDetailPageIntent(pkg: String?): Intent? {
    var intent: Intent? = null
    if (isAppInstalled(pkg)) {
        pkg?.let {
            intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent?.setData(Uri.fromParts("package", it, null))
        }
    }
    return intent
}

fun getAppMarketDetailPageIntent(pkg: String?, marketPkg: String? = null): Intent? {
    var intent: Intent? = null
    if (isAppInstalled(pkg)) {
        pkg?.let {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$it"))
            marketPkg?.let { intent?.setPackage(marketPkg) }
        }
    }
    return intent
}

fun getAppInstallIntent(path: String?): Intent? {
    var intent: Intent? = null
    path?.let {
        intent = Intent(Intent.ACTION_VIEW)
        intent?.setDataAndType(Uri.fromFile(File(it)), "application/vnd.android.package-archive")
    }
    return intent
}

fun getAppUninstallIntent(pkg: String?): Intent? {
    var intent: Intent? = null
    if (isAppInstalled(pkg)) {
        pkg?.let {
            intent = Intent(Intent.ACTION_DELETE)
            intent?.data = Uri.parse("package:$it")
        }
    }
    return intent
}

const val MSG_APP_GET_SIZE_FAILED = 0
const val MSG_APP_GET_SIZE_SUCCESS = 1

const val APP_GET_SIZE_FAILED = "app.get.size.failed"
const val APP_CACHE_SIZE = "app.cache.size"
const val APP_DATA_SIZE = "app.data.size"
const val APP_CODE_SIZE = "app.code.size"

fun getAppSize(pkg: String?, handler: Handler) {
    Log.d(TAG, "getAppSize()...pkg:$pkg")
    val data = Bundle()
    if (pkg == null) {
        data.putString(APP_GET_SIZE_FAILED, "pkg is null")
        val msg = handler.obtainMessage()
        msg.what = MSG_APP_GET_SIZE_FAILED
        msg.data = data
        handler.sendMessage(msg)
        return
    }
    val mPackManager: PackageManager = App.context.packageManager
    //反射获取PackageManager内部getPackageSizeInfo方法
    val method = PackageManager::class.java.getMethod(
        "getPackageSizeInfo",
        String::class.java,
        IPackageStatsObserver::class.java
    )
    method.invoke(mPackManager, pkg, object : IPackageStatsObserver.Stub() {
        override fun onGetStatsCompleted(pStats: PackageStats?, succeeded: Boolean) {
            //应用的总大小等于缓存大小加上数据大小再加上应用的大小
            pStats?.let {
                data.putLong(APP_CACHE_SIZE, pStats.cacheSize)
                data.putLong(APP_DATA_SIZE, pStats.dataSize)
                data.putLong(APP_CODE_SIZE, pStats.codeSize)
                val msg = handler.obtainMessage()
                msg.what = MSG_APP_GET_SIZE_SUCCESS
                msg.data = data
                handler.sendMessage(msg)
            }
        }
    })
}

fun isSystemApp(pkg: String): Boolean {
    val pkgInfo = App.context.packageManager.getPackageInfo(pkg, 0)
    return pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM > 0
}

fun isAppDebuggable(pkg: String): Boolean {
    val pkgInfo = App.context.packageManager.getPackageInfo(pkg, 0)
    return pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
}

fun getStartActivityName(pkg: String?): MutableList<String> {
    val result = mutableListOf<String>()
    pkg?.let {
        val resolveIntent = Intent(Intent.ACTION_MAIN, null)
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        resolveIntent.setPackage(pkg)
        val resolveResults = App.context.packageManager.queryIntentActivities(resolveIntent, 0)
        Log.d("入口", "initData()...resolveResults:$resolveResults")

        for (item in resolveResults) {
            Log.d("入口", "initData()...pkg:${item.activityInfo.packageName}")
            Log.d("入口", "initData()...class:${item.activityInfo.name}")

            result.add(item.activityInfo.name)
        }
    }
    return result
}

@RequiresApi(Build.VERSION_CODES.Q)
fun isAppRunning(pkgName: String): Boolean {
    val uid: Int
    val packageManager = App.context.packageManager
    try {
        val ai = packageManager.getApplicationInfo(pkgName, 0) ?: return false
        uid = ai.uid
    } catch (e: NameNotFoundException) {
        e.printStackTrace()
        return false
    }

    val am = App.context.getSystemService(ACTIVITY_SERVICE) as ActivityManager?
    if (am != null) {
        val taskInfo = am.getRunningTasks(Integer.MAX_VALUE)
        if (taskInfo != null && taskInfo.size > 0) {
            for (aInfo in taskInfo) {
                if (pkgName == aInfo.baseActivity?.packageName) {
                    return true
                }
            }
        }
        val serviceInfo = am.getRunningServices(Integer.MAX_VALUE)
        if (serviceInfo != null && serviceInfo.size > 0) {
            for (aInfo in serviceInfo) {
                if (uid == aInfo.uid) {
                    return true
                }
            }
        }
    }
    return false
}

@RequiresApi(Build.VERSION_CODES.P)
@Throws(NameNotFoundException::class)
fun getAppSignatures(pkg: String): SigningInfo {
    val pkgInfo = App.context.packageManager.getPackageInfo(pkg, GET_SIGNING_CERTIFICATES)
    return pkgInfo.signingInfo
}

@TargetApi(Build.VERSION_CODES.O_MR1)
@Throws(NameNotFoundException::class)
fun getAppSignaturesBelow28(pkg: String): Array<Signature> {
    val pkgInfo = App.context.packageManager.getPackageInfo(pkg, GET_SIGNATURES)
    return pkgInfo.signatures
}

/**
 * algorithm accept MD5,SHA1,SHA256
 */
fun getAppSignature(pkg: String, algorithm: String): String {
    var result = ""
    val signBytes = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        val signatures = getAppSignaturesBelow28(pkg)

        Log.d("签名信息getAppSignature", "below 28:${signatures[0]}")
        val bytes = signatures[0].toByteArray()
        val buffer = StringBuffer()
        for (byte in bytes) {
            buffer.append("$byte")
        }
        Log.d("签名信息getAppSignature", "below 28 toByteArray:$buffer")
        signatures[0].toByteArray()
    } else {
        val signingInfo = getAppSignatures(pkg)
        Log.d("签名信息getAppSignature", "target 28:${signingInfo.apkContentsSigners}")
        for (signature in signingInfo.apkContentsSigners) {
            Log.d("签名信息getAppSignature", "target 28...signature:${signature.toCharsString()}")
        }
        signingInfo.apkContentsSigners[0].toByteArray()
    }
    when (algorithm) {
        MD5 -> result = encryptToMD5(signBytes)
        SHA1 -> result = encryptToSHA1(signBytes)
        SHA256 -> result = encryptToSHA256(signBytes)
    }
    Log.d("签名信息getAppSignature", "self after digest encryptToMD5:$result")
    return result
}