package com.floatingmuseum.app.analyser.ui

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.floatingmuseum.app.analyser.R
import com.floatingmuseum.app.analyser.utils.*
import kotlinx.android.synthetic.main.activity_screen.*
import kotlin.math.sqrt


/**
 * Created by Floatingmuseum on 2019-12-09.
 *
 * DPI:像素密度，指的是在系统软件上指定的单位尺寸的像素数量
 *
 */
class ScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen)

        initView()
    }

    private fun initView() {
        val dpi = getDPI()
        val realScreenSizePx = getRealScreenSizeInPx()
        val realScreenWidthPx = realScreenSizePx?.first ?: -1
        val realScreenHeightPx = realScreenSizePx?.second ?: -1
        val realScreenSizeDp = getRealScreenSizeInDp()
        val realScreenWidthDp = scaleDecimal(realScreenSizeDp?.first ?: (-1).toFloat(), 1)
        val realScreenHeightDp = scaleDecimal(realScreenSizeDp?.second ?: (-1).toFloat(), 1)
        val usableScreenSizePx = getUsableScreenSizeInPx()
        val usableScreenWidthPx = usableScreenSizePx?.first ?: -1
        val usableScreenHeightPx = usableScreenSizePx?.second ?: -1
        val usableScreenSizeDp = getUsableScreenSizeInDp()
        val usableScreenWidthDp = scaleDecimal(usableScreenSizeDp?.first ?: (-1).toFloat(), 1)
        val usableScreenHeightDp = scaleDecimal(usableScreenSizeDp?.second ?: (-1).toFloat(), 1)

        val statusBarHeightPx = getStatusBarHeightInPx()
        val navBarHeightPx = getNavBarHeightInPx()

        val statusBarHeightDp = getStatusBarHeightInDp()
        val navBarHeightDp = getNavBarHeightInDp()

        val pxPerDp = dpi.toFloat() / 160.toFloat()
        val floatScreenInch =
            sqrt((realScreenWidthPx * realScreenWidthPx + realScreenHeightPx * realScreenHeightPx).toDouble()) / dpi
        val screenInch = scaleDecimal(floatScreenInch, 1)

        val orientation = when (getScreenOrientation()) {
            Configuration.ORIENTATION_LANDSCAPE -> "横向"
            Configuration.ORIENTATION_PORTRAIT -> "竖向"
            else -> "未知"
        }

        val screenScale = minScale(realScreenWidthPx, realScreenHeightPx)

        val info = """
            DPI:$dpi/${getDPIText()}
            尺寸:${screenInch}英寸 
            屏幕方向:$orientation
            屏幕宽高比:${screenScale.first}:${screenScale.second}
            
            px
            完整宽高(px):${realScreenWidthPx}x$realScreenHeightPx
            实际宽高(px):${usableScreenWidthPx}x$usableScreenHeightPx
            状态栏高度(px):$statusBarHeightPx
            导航栏高度(px):$navBarHeightPx
            
            dp
            完整宽高(dp):${realScreenWidthDp}x$realScreenHeightDp
            实际宽高(dp):${usableScreenWidthDp}x$usableScreenHeightDp
            状态栏高度(dp):$statusBarHeightDp
            导航栏高度(dp):$navBarHeightDp
            
            dp与px比例:1dp=${pxPerDp}px
        """.trimIndent()

        tv_screen_info.text = info

        val config = resources.configuration

        showConfig(config)

        tv_screen_adaptation_advice.text = """
            依smallestWidth适配
            在res下创建values-sw${config.smallestScreenWidthDp}dp文件夹
            
            依宽度适配
            在res下创建values-w${config.screenWidthDp}dp文件夹
            
            依高度适配
            在res下创建values-h${config.screenHeightDp}dp文件夹
        """.trimIndent()
    }

    private fun showConfig(config: Configuration?) {
        Log.d("屏幕Config", "fontScale:${config?.fontScale}")
        Log.d("屏幕Config", "smallestScreenWidthDp:${config?.smallestScreenWidthDp}")
        Log.d("屏幕Config", "densityDpi:${config?.densityDpi}")
        Log.d("屏幕Config", "hardKeyboardHidden:${config?.hardKeyboardHidden}")
        Log.d("屏幕Config", "keyboard:${config?.keyboard}")
        Log.d("屏幕Config", "keyboardHidden:${config?.keyboardHidden}")
        Log.d("屏幕Config", "layoutDirection:${config?.layoutDirection}")
        Log.d("屏幕Config", "mcc:${config?.mcc}")
        Log.d("屏幕Config", "mnc:${config?.mnc}")
        Log.d("屏幕Config", "navigation:${config?.navigation}")
        Log.d("屏幕Config", "navigationHidden:${config?.navigationHidden}")
        Log.d("屏幕Config", "orientation:${config?.orientation}")
        Log.d("屏幕Config", "screenHeightDp:${config?.screenHeightDp}")
        Log.d("屏幕Config", "screenWidthDp:${config?.screenWidthDp}")
        Log.d("屏幕Config", "screenLayout:${config?.screenLayout}")
        Log.d("屏幕Config", "touchscreen:${config?.touchscreen}")
        Log.d("屏幕Config", "uiMode:${config?.uiMode}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("屏幕Config", "isScreenRound:${config?.isScreenRound}")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("屏幕Config", "isScreenWideColorGamut:${config?.isScreenWideColorGamut}")
            Log.d("屏幕Config", "isScreenHdr:${config?.isScreenHdr}")
            Log.d("屏幕Config", "colorMode:${config?.colorMode}")
        }

    }
}