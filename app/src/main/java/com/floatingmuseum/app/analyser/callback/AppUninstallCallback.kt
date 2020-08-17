package com.floatingmuseum.app.analyser.callback

/**
 * Created by Floatingmuseum on 2019-12-02.
 */
interface AppUninstallCallback {

    fun onAppUninstall(uninstallPkg: String)
}