package com.floatingmuseum.app.analyser.ui.main

import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.floatingmuseum.app.analyser.App
import com.floatingmuseum.app.analyser.R
import com.floatingmuseum.app.analyser.manager.SystemInfoManager
import com.floatingmuseum.app.analyser.utils.getSdkVersion
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Floatingmuseum on 2019-12-31.
 */
class MainViewModel : ViewModel() {
    private val tag = "MainViewModel"
    private var systemInfoDisposable: Disposable? = null
    private var screenInfoDisposable: Disposable? = null
    private val systemInfo = MutableLiveData<String>()
    private val screenInfo = MutableLiveData<String>()

    fun getSystemInfo() = systemInfo

    fun getScreenInfo() = screenInfo

    fun start() {
        Log.d(tag, "start()")
        systemInfoDisposable = Observable.create<String> {
            val sdkVersion = getSdkVersion(Build.VERSION.SDK_INT)
            val result = """
            系统信息    
            品牌:${Build.BRAND}
            Android版本:${App.context.getString(
                R.string.sdk_ver,
                sdkVersion.first,
                sdkVersion.second,
                sdkVersion.third
            )}
            设备名称:${Build.DEVICE}
            设备型号:${Build.MODEL}
            系统版本号:${Build.DISPLAY}
        """.trimIndent()
            it.onNext(result)
        }.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(tag, "start()...update...data:$it")
                    systemInfo.value = it
                },
                {
                    Log.e(tag, "start()...error...:${it.message}")
                    it.printStackTrace()
                }
            )

        systemInfoDisposable = SystemInfoManager.getSystemInfo()
            .subscribe(
                {
                    Log.d(tag, "start()...update...data:$it")
                    systemInfo.value = it
                },
                {
                    Log.e(tag, "start()...error...:${it.message}")
                    it.printStackTrace()
                }
            )
    }

    fun stop() {
        systemInfoDisposable?.dispose()
        screenInfoDisposable?.dispose()
    }
}