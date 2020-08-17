package com.floatingmuseum.app.analyser.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.floatingmuseum.app.analyser.R
import com.floatingmuseum.app.analyser.utils.getSdkVersion
import kotlinx.android.synthetic.main.activity_system.*

/**
 * Created by Floatingmuseum on 2019-12-09.
 */
class SystemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system)

        initView()
    }

    private fun initView() {
        val sdkVersion = getSdkVersion(Build.VERSION.SDK_INT)
        tv_system_info.text = """
            品牌:${Build.BRAND}
            Android版本:${resources.getString(R.string.sdk_ver, sdkVersion.first, sdkVersion.second, sdkVersion.third)}
            设备名称:${Build.DEVICE}
            设备型号:${Build.MODEL}
            系统版本号:${Build.DISPLAY}
        """.trimIndent()
    }
}