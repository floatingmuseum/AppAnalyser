package com.floatingmuseum.app.analyser.utils

import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Floatingmuseum on 2019-12-31.
 */

private val schedulerTransformer = ObservableTransformer<Any, Any> { upstream ->
    upstream.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * 子线程/主线程切换
 */
fun <T> threadSwitch(): ObservableTransformer<T, T> {
    return schedulerTransformer as ObservableTransformer<T, T>
}

private val FlowableSchedulerTransformer = FlowableTransformer<Any, Any> { upstream ->
    upstream.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * 子线程/主线程切换
 */
fun <T> flowableThreadSwitch(): FlowableTransformer<T, T> {
    return FlowableSchedulerTransformer as FlowableTransformer<T, T>
}