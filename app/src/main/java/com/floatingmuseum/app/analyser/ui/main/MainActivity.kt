package com.floatingmuseum.app.analyser.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.floatingmuseum.app.analyser.App
import com.floatingmuseum.app.analyser.R
import com.floatingmuseum.app.analyser.ui.AboutActivity
import com.floatingmuseum.app.analyser.ui.AppActivity
import com.floatingmuseum.app.analyser.ui.ScreenActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initObserver()
    }

    private fun initView() {
        tv_screen.setOnClickListener { startPage(ScreenActivity::class.java) }
        tv_app.setOnClickListener { startPage(AppActivity::class.java) }
        tv_about.setOnClickListener { startPage(AboutActivity::class.java) }
    }

    private fun initObserver() {
        Log.d(TAG, "initObserver()")
        mainViewModel.getSystemInfo().observe(this, Observer {
            tv_system.text = it
        })
        getTotalSize()
    }

    fun getParentFile(): File? {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            Log.d("获取SD卡总大小", "getParentFile:...above Android P")
            App.context.getExternalFilesDir(null)
        } else {
            Environment.getExternalStorageDirectory()
        }
    }

    private fun getTotalSize() {
        try {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val path = getParentFile()
                val statFs = StatFs(path?.path)
                val blockSize = statFs.blockSizeLong
                val totalBlocks = statFs.blockCountLong
                // 获取sd卡的总内存大小
                Log.d("获取SD卡总大小", "raw容量:${blockSize * totalBlocks}")
                Log.d(
                    "获取SD卡总大小",
                    "容量:${java.lang.Long.valueOf(blockSize * totalBlocks / 1024 / 1024)}"
                )
            } else {
                Log.e("获取SD卡总大小", "错误...未挂载")
            }
        } catch (e: Exception) {
            //在SA32的平板(并不知道具体是什么平板)上出现此异常Invalid path: /storage/sdcard
            Log.e("获取SD卡总大小", "=错误..." + e.message)
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.start()
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.stop()
    }

    private fun <T> startPage(cls: Class<T>) {
        startActivity(Intent(this, cls))
    }
}
