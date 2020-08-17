package com.floatingmuseum.app.analyser.manager

import android.os.Build
import com.floatingmuseum.app.analyser.App
import com.floatingmuseum.app.analyser.R
import com.floatingmuseum.app.analyser.utils.getSdkVersion
import com.floatingmuseum.app.analyser.utils.threadSwitch
import io.reactivex.Observable

/**
 * Created by Floatingmuseum on 2019-12-31.
 */
object SystemInfoManager {

    fun getSystemInfo(): Observable<String> {
        return Observable.create<String> {
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
        }
            .compose(threadSwitch())
    }
}