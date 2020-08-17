package com.floatingmuseum.app.analyser.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.floatingmuseum.app.analyser.BuildConfig
import com.floatingmuseum.app.analyser.R
import kotlinx.android.synthetic.main.item_app.*

/**
 * Created by Floatingmuseum on 2019-11-07.
 */
class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        initUI()
    }

    private fun initUI() {
        iv_icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_icon2))
        tv_name.text = "名称:${getText(R.string.app_name)}"
        tv_package_name.text = "包名:${BuildConfig.APPLICATION_ID}"
        tv_ver_name.text = "版本名:${BuildConfig.VERSION_NAME}"
    }
}