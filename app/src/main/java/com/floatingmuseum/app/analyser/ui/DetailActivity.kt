package com.floatingmuseum.app.analyser.ui

import android.Manifest
import android.app.Activity
import android.app.usage.StorageStatsManager
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.net.toUri
import com.floatingmuseum.app.analyser.App
import com.floatingmuseum.app.analyser.R
import com.floatingmuseum.app.analyser.utils.*
import kotlinx.android.synthetic.main.activity_detail.*
import java.io.File
import java.lang.StringBuilder
import java.util.*

/**
 * Created by Floatingmuseum on 2019-10-04.
 */
class DetailActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DetailActivity"
        private const val REQUEST_CODE_CHECK_USAGE_STATS = 101
        private const val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 102
        private const val REQUEST_CODE_CREATE_NEW_FILE = 103
        const val DETAIL_PKG_NAME = "detail_pkg_name"
    }

    private lateinit var pkg: String
    private var appSizeHandler: Handler? = null
    private var pkgInfo: PackageInfo? = null
    private var apkSourcePath: String? = null
    private var sourcePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        intent.getStringExtra(DETAIL_PKG_NAME)?.let {
            if (it.isNotBlank()) {
                pkg = it
                val startTime1 = System.currentTimeMillis()
                initView(pkg)
                Log.d("耗时", "测试1:${System.currentTimeMillis() - startTime1}")
                val startTime2 = System.currentTimeMillis()
                initData(pkg)
                Log.d("耗时", "测试2:${System.currentTimeMillis() - startTime2}")
            }
        }
    }

    private fun initView(pkg: String?) {
        tv_start_app.setOnClickListener {
            val intent = getLauncherIntent(pkg)
            try {
                intent?.let { startActivity(intent) }
            } catch (e: Exception) {
                toastShort(App.context, "启动应用失败:$e")
                e.printStackTrace()
            }
        }
        tv_app_setting_page.setOnClickListener {
            val intent = getAppSettingsDetailPageIntent(pkg)
            intent?.let { startActivity(intent) }
        }
        tv_app_export.setOnClickListener {
            exportApp()
        }
        tv_app_uninstall.setOnClickListener {
            val intent = getAppUninstallIntent(pkg)
            intent?.let { startActivity(intent) }
        }
        tv_check_from_market.setOnClickListener {
            val intent = getAppMarketDetailPageIntent(pkg)
            try {
                intent?.let { startActivity(intent) }
            } catch (e: Exception) {
                toastShort(App.context, "启动应用中心异常:$e")
                e.printStackTrace()
            }
        }
        tv_share_app.setOnClickListener {
            shareApp()
        }
        tv_permissions_title.setOnClickListener {
            showPermissionsInfo()
        }
        tv_activities_title.setOnClickListener {
            showActivitiesInfo()
        }
        tv_receivers_title.setOnClickListener {
            showReceiversInfo()
        }
        tv_services_title.setOnClickListener {
            showServicesInfo()
        }
        tv_providers_title.setOnClickListener {
            showProvidersInfo()
        }
    }

    private fun initData(pkg: String?) {
        Log.d(TAG, "initData()...pkg:$pkg")
        if (pkg != null) {
            val packageInfo = getPackageInfo(pkg)
            packageInfo?.let {
                pkgInfo = it
                if (!it.applicationInfo.enabled) {
                    tv_disable_app_reminder.visibility = View.VISIBLE
                }
                iv_icon.setImageDrawable(it.applicationInfo.loadIcon(packageManager))
                tv_name.text = it.applicationInfo.loadLabel(packageManager)
                tv_package_name.text = it.packageName
                tv_is_system.text = if (isSystemApp(pkg)) "是" else "否"
                tv_is_debuggable.text = if (isAppDebuggable(pkg)) "是" else "否"

                tv_version_name.text = it.versionName
                tv_version_code.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    it.longVersionCode.toString()
                } else {
                    it.versionCode.toString()
                }
                apkSourcePath = it.applicationInfo.sourceDir
                tv_apk_source_dir.text = apkSourcePath
                tv_native_dir.text = it.applicationInfo.nativeLibraryDir
                updateApkSize()
                updateAppSize()
                tv_first_install_time.text = formatTime(it.firstInstallTime)
                tv_last_update_time.text = formatTime(it.lastUpdateTime)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val minSdks = getSdkVersion(it.applicationInfo.minSdkVersion)
                    tv_min_sdk.text = resources.getString(
                        R.string.min_sdk,
                        minSdks.first,
                        minSdks.second,
                        minSdks.third
                    )
                }
                val targetSdks = getSdkVersion(it.applicationInfo.targetSdkVersion)
                tv_target_sdk.text = resources.getString(
                    R.string.target_sdk,
                    targetSdks.first,
                    targetSdks.second,
                    targetSdks.third
                )
                val signatureMD5 = splitSignature(getAppSignature(pkg, MD5))
                val signatureSHA1 = splitSignature(getAppSignature(pkg, SHA1))
                val signatureSHA256 = splitSignature(getAppSignature(pkg, SHA256))
                tv_signatures.text = getString(
                    R.string.hash_signatures,
                    signatureMD5,
                    signatureSHA1,
                    signatureSHA256
                )
                tv_app_uid.text = "${it.applicationInfo.uid}"
                tv_app_data_dir.text = it.applicationInfo.dataDir
                val startActivities = getStartActivityName(pkg)

                if (startActivities.isEmpty()) {
                    tv_main_activity.text = getString(R.string.nothing)
                } else {
                    tv_main_activity.text = separateString(startActivities, "\n")
                }

                tv_permissions_title.text =
                    resources.getString(R.string.permissions, it.requestedPermissions?.size ?: 0)
                tv_activities_title.text =
                    resources.getString(R.string.activities, it.activities?.size ?: 0)
                tv_receivers_title.text =
                    resources.getString(R.string.receivers, it.receivers?.size ?: 0)
                tv_services_title.text =
                    resources.getString(R.string.services, it.services?.size ?: 0)
                tv_providers_title.text =
                    resources.getString(R.string.providers, it.providers?.size ?: 0)
            }
        }
    }

    private fun shareApp() {
        apkSourcePath?.let {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_STREAM, it.toUri())
            shareIntent.type = "application/vnd.android.package-archive"
            startActivity(shareIntent)
        }
    }

    private fun splitSignature(signature: String): String {
        return if (signature.isEmpty()) {
            return signature
        } else {
            val buffer = StringBuffer()
            signature.forEachIndexed { index, char ->
                buffer.append(if (index % 2 == 1 && index + 1 != signature.length) "$char:" else char)
            }
            buffer.toString()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        Log.d(TAG, "onActivityResult()...requestCode:$requestCode...resultCode:$resultCode")
        when (requestCode) {
            REQUEST_CODE_CHECK_USAGE_STATS -> updateAppSize()
            REQUEST_CODE_WRITE_EXTERNAL_STORAGE -> {
            }
            REQUEST_CODE_CREATE_NEW_FILE -> {
                Log.d(
                    TAG,
                    "onActivityResult()...REQUEST_CODE_CREATE_NEW_FILE:...uri:${resultData?.data}"
                )
                if (Activity.RESULT_OK == resultCode)
                    resultData?.data?.let { uri ->
                        sourcePath?.let { source ->
                            if (copyFile(source, uri)) {
                                toastLong(App.context, "保存到:$sourcePath")
                            }
                        }
                    }
            }
        }
    }

    private fun updateApkSize() {
        apkSourcePath?.let {
            if (it.isNotBlank()) {
                val file = File(it)
                val size = formatFileSize(file.length())
                tv_apk_size.text = size
            }
        }
    }

    private fun updateAppSize() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Log.d(TAG, "pkg:$pkg...getAppSize...below 26")

            appSizeHandler = Handler { msg ->
                when (msg.what) {
                    MSG_APP_GET_SIZE_SUCCESS -> {
                        val cacheSize = msg.data.getLong(APP_CACHE_SIZE)
                        val dataSize = msg.data.getLong(APP_DATA_SIZE)
                        val codeSize = msg.data.getLong(APP_CODE_SIZE)
                        Log.d(TAG, "initData()...getAppSize....cacheSize:$cacheSize")
                        Log.d(TAG, "initData()...getAppSize....dataSize:$dataSize")
                        Log.d(TAG, "initData()...getAppSize....codeSize:$codeSize")
                        val appSize = cacheSize + dataSize + codeSize
                        Log.d(TAG, "initData()...pkg:$pkg...getAppSize....appSize:$appSize")
                        tv_app_size.text = formatFileSize(appSize)
                    }
                    MSG_APP_GET_SIZE_FAILED -> {
                        val failedReason = msg.data.getString(APP_GET_SIZE_FAILED)
                        Log.d(TAG, "getAppSize...failed:$failedReason")
                    }
                }

                true
            }
            appSizeHandler?.let { handler -> getAppSize(pkg, handler) }
        } else {
            Log.d(TAG, "pkg:$pkg...getAppSize...26...usage permission:${checkUsageStats()}")
            if (checkUsageStats()) {
                getPackageInfo(pkg)?.let {
                    tv_app_size.text = formatFileSize(getAppSize(it.applicationInfo.uid))
                }
            } else {
                tv_app_size.text = getString(R.string.need_check_usage_stats_permission)
                if (!tv_app_size.hasOnClickListeners()) {
                    tv_app_size.setOnClickListener {
                        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                        startActivityForResult(
                            intent,
                            REQUEST_CODE_CHECK_USAGE_STATS
                        )
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.PACKAGE_USAGE_STATS)
    private fun getAppSize(uid: Int): Long {
        var appSize = (-1).toLong()
        val storageStatsManager =
            getSystemService(STORAGE_STATS_SERVICE) as StorageStatsManager
        val storageManager = getSystemService(STORAGE_SERVICE) as StorageManager
        //获取所有应用的StorageVolume列表
        val storageVolumes: List<StorageVolume> = storageManager.storageVolumes
        for (item in storageVolumes) {
            val uuidStr = item.uuid
            val uuid: UUID =
                if (uuidStr == null) StorageManager.UUID_DEFAULT else UUID.fromString(
                    uuidStr
                )
            val storageStats = storageStatsManager.queryStatsForUid(uuid, uid)
            //获取到App的总大小
            appSize = storageStats.appBytes + storageStats.cacheBytes + storageStats.dataBytes
            Log.d(TAG, "getAppSize()...app size:$appSize")

        }
        return appSize
    }

    private fun exportApp() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && !hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_WRITE_EXTERNAL_STORAGE
            )
            return
        }

        if (!isExternalStorageWritable()) {
            toastShort(App.context, "无法对外部存储进行写入")
            return
        }

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            // Create a file with the requested MIME type.
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_TITLE, pkg)
        }

        pkgInfo?.let {
            sourcePath = it.applicationInfo.publicSourceDir
        }
        if (!sourcePath.isNullOrBlank()) {
            startActivityForResult(
                intent,
                REQUEST_CODE_CREATE_NEW_FILE
            )
        }
    }

    private fun showPermissionsInfo() {
        pkgInfo?.let {
            val itemSize = it.requestedPermissions?.size ?: 0
            tv_permissions_title.text = resources.getString(R.string.permissions, itemSize)
            if (itemSize > 0) {
                it.requestedPermissions?.let { permissions ->
                    ll_permissions_container.visibility = View.VISIBLE
                    val contentBuilder = StringBuilder()

                    permissions.forEachIndexed { index, info ->
                        val content = if (index == permissions.size - 1) {
                            info
                        } else {
                            "$info\n"
                        }
                        contentBuilder.append(content)
                    }
                    fillContent(ll_permissions_container, contentBuilder.toString())
                }
            }
        }
    }

    private fun showActivitiesInfo() {
        pkgInfo?.let {
            val itemSize = it.activities?.size ?: 0
            tv_activities_title.text = resources.getString(R.string.activities, itemSize)
            if (itemSize > 0) {
                it.activities?.let { activities ->
                    ll_activities_container.visibility = View.VISIBLE
                    val contentBuilder = StringBuilder()

                    activities.forEachIndexed { index, info ->
                        val content = if (index == activities.size - 1) {
                            info.name
                        } else {
                            "${info.name}\n"
                        }
                        contentBuilder.append(content)
                    }

                    fillContent(ll_activities_container, contentBuilder.toString())
                }
            }
        }
    }

    private fun showReceiversInfo() {
        pkgInfo?.let {
            val itemSize = it.receivers?.size ?: 0
            tv_receivers_title.text = resources.getString(R.string.receivers, itemSize)
            if (itemSize > 0) {
                it.receivers?.let { receivers ->
                    ll_receivers_container.visibility = View.VISIBLE
                    val contentBuilder = StringBuilder()

                    receivers.forEachIndexed { index, info ->
                        val content = if (index == receivers.size - 1) {
                            info.name
                        } else {
                            "${info.name}\n"
                        }
                        contentBuilder.append(content)
                    }

                    fillContent(ll_receivers_container, contentBuilder.toString())
                }
            }
        }
    }

    private fun showServicesInfo() {
        pkgInfo?.let {
            val itemSize = it.services?.size ?: 0
            tv_services_title.text = resources.getString(R.string.services, itemSize)
            if (itemSize > 0) {
                it.services?.let { services ->
                    ll_services_container.visibility = View.VISIBLE
                    val contentBuilder = StringBuilder()

                    services.forEachIndexed { index, info ->
                        val content = if (index == services.size - 1) {
                            info.name
                        } else {
                            "${info.name}\n"
                        }
                        contentBuilder.append(content)
                    }

                    fillContent(ll_services_container, contentBuilder.toString())
                }
            }
        }
    }

    private fun showProvidersInfo() {
        pkgInfo?.let {
            val itemSize = it.providers?.size ?: 0
            tv_providers_title.text = resources.getString(R.string.providers, itemSize)
            if (itemSize > 0) {
                it.providers?.let { providers ->
                    ll_providers_container.visibility = View.VISIBLE
                    val contentBuilder = StringBuilder()

                    providers.forEachIndexed { index, info ->
                        val content = if (index == providers.size - 1) {
                            info.name
                        } else {
                            "${info.name}\n"
                        }
                        contentBuilder.append(content)
                    }

                    fillContent(ll_providers_container, contentBuilder.toString())
                }
            }
        }
    }

    private fun fillContent(container: LinearLayoutCompat, content: String) {
        val contentView = LayoutInflater.from(this).inflate(
            R.layout.detail_content_layout,
            null
        ) as TextView
        contentView.text = content
        container.addView(contentView)
    }

    override fun onDestroy() {
        super.onDestroy()
        appSizeHandler?.removeCallbacksAndMessages(null)
        appSizeHandler = null
    }
}