package com.floatingmuseum.app.analyser

import android.app.Application
import com.floatingmuseum.app.analyser.utils.SPUtil

/**
 * Created by Floatingmuseum on 2019-10-03.
 */
class App : Application() {

    companion object{
        lateinit var context: Application
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        SPUtil.init(this)
    }
}