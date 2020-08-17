package com.floatingmuseum.app.analyser.ui

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.floatingmuseum.app.analyser.R
import com.floatingmuseum.app.analyser.callback.AppUninstallCallback
import com.floatingmuseum.app.analyser.entity.AppItem
import com.floatingmuseum.app.analyser.receiver.AppUninstallReceiver
import com.floatingmuseum.app.analyser.utils.*
import com.uber.autodispose.android.lifecycle.autoDispose
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_app.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by Floatingmuseum on 2019-12-09.
 */
class AppActivity : AppCompatActivity(), AppUninstallCallback {

    companion object {
        private const val TAG = "AppActivity"
        private const val SHOW_TV_STATE = 0
        private const val SHOW_RV_CONTAINER = 1
        private const val SHOW_RV_SEARCH_RESULTS_CONTAINER = 2

        const val TYPE_APP_SYSTEM = 0
        const val TYPE_APP_THIRD = 1
    }

    private var size = 0
    private var analyzedSize = 0
    private var loadAppListJob: Job? = null
    private var uninstallReceiver: AppUninstallReceiver? = null
    private val data = mutableListOf<AppItem>()
    private val searchData = mutableListOf<AppItem>()
    private val dataMap = hashMapOf<Int, MutableList<AppItem>>(
        TYPE_APP_SYSTEM to mutableListOf(),
        TYPE_APP_THIRD to mutableListOf()
    )
    private lateinit var adapter: AppAdapter
    private lateinit var searchResultsAdapter: SearchResultsAdapter
    private var shouldContainsSystemApp = SPUtil.getBoolean(SP_KEY_CONTAINS_SYSTEM_APP, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        initView()
        loadDataV2()
    }

    private fun initView() {
        cb_include_system_app.isChecked = shouldContainsSystemApp
        adapter = AppAdapter(data)
        rv_container.adapter = adapter
        rv_container.layoutManager = LinearLayoutManager(this)
        rv_container.addOnItemTouchListener(object : OnItemClickListener() {
            override fun onSimpleItemClick(
                adapter: BaseQuickAdapter<*, *>?,
                view: View?,
                position: Int
            ) {
                Log.d("点击", "OnItemClickListener()...position:$position")
                val intent = Intent(this@AppActivity, AppDetailActivity::class.java)
                intent.putExtra(AppDetailActivity.DETAIL_PKG_NAME, data[position].pkg)
                startActivity(intent)
            }
        })

        searchResultsAdapter = SearchResultsAdapter(searchData)
        rv_search_results_container.adapter = searchResultsAdapter
        rv_search_results_container.layoutManager = LinearLayoutManager(this)
        rv_search_results_container.addOnItemTouchListener(object : OnItemClickListener() {
            override fun onSimpleItemClick(
                adapter: BaseQuickAdapter<*, *>?,
                view: View?,
                position: Int
            ) {
                val intent = Intent(this@AppActivity, AppDetailActivity::class.java)
                intent.putExtra(AppDetailActivity.DETAIL_PKG_NAME, searchData[position].pkg)
                startActivity(intent)
            }
        })

        cb_include_system_app.setOnCheckedChangeListener { _, isChecked ->
            Log.d(TAG, "searchTest...onCheckChange()...isChecked:$isChecked")
            cb_include_system_app.isEnabled = false
            SPUtil.putBoolean(SP_KEY_CONTAINS_SYSTEM_APP, isChecked)
            shouldContainsSystemApp = isChecked
            updateAppList()
        }

        et_search.doOnTextChanged { text, _, _, _ ->
            Log.d(TAG, "searchTest...doOnTextChanged()...newText:$text")
            searchAppList(text?.toString())
        }
        et_search.setOnFocusChangeListener { v, hasFocus ->
            Log.d(TAG, "searchTest...et_search...setOnFocusChangeListener()...hasFocus:$hasFocus")
        }
    }

    private fun loadDataV2() {
        loadAppListJob = GlobalScope.launch(Dispatchers.IO) {
            val allInstallApp = getInstallPackageInfo()
            size = allInstallApp.size
            allInstallApp.forEach { rawApp ->
                val app = convertPackageInfoToAppItem(rawApp)
                //更新加载进度
                withContext(Dispatchers.Main) {
                    analyzedSize++
                    tv_state.text = "加载中...$analyzedSize/$size"
                }
                if (app.isSystem) {
                    dataMap[TYPE_APP_SYSTEM]?.add(app)
                } else {
                    dataMap[TYPE_APP_THIRD]?.add(app)
                }
            }

            withContext(Dispatchers.Main) {
                initHeaderView()
                updateAppList()
                ll_bottom_box.visibility = View.VISIBLE
                cb_include_system_app.isEnabled = true
                setVisibility(SHOW_RV_CONTAINER)
                registerUninstallReceiver()
            }
        }
    }

    private fun convertPackageInfoToAppItem(info: PackageInfo): AppItem {
        val name = info.applicationInfo.loadLabel(packageManager).toString()
        val pkg = info.packageName
        val icon = info.applicationInfo.loadIcon(packageManager)
        val verName = info.versionName
        val isSystem = (info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) > 0
        return AppItem(name, pkg, icon, verName, isSystem)
    }

    private fun registerUninstallReceiver() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
        filter.addDataScheme("package")
        uninstallReceiver = AppUninstallReceiver(this)
        registerReceiver(uninstallReceiver, filter)
    }

    private fun initHeaderView() {
        val headerView = LayoutInflater.from(this).inflate(R.layout.item_main_header, null)
        val thirdAppNum = "第三方应用:${dataMap[TYPE_APP_THIRD]?.size ?: 0}个"
        headerView.findViewById<TextView>(R.id.tv_third_app).text = thirdAppNum
        val systemAppNum = "系统应用:${dataMap[TYPE_APP_SYSTEM]?.size ?: 0}个"
        headerView.findViewById<TextView>(R.id.tv_system_app).text = systemAppNum
        adapter.setHeaderView(headerView)
    }

    private fun updateAppList() {
        data.clear()
        dataMap[TYPE_APP_THIRD]?.let {
            val sortedList = it.sortedBy { app -> app.name }
            data.addAll(sortedList)
        }
        if (shouldContainsSystemApp) {
            dataMap[TYPE_APP_SYSTEM]?.let {
                val sortedList = it.sortedBy { app -> app.name }
                data.addAll(sortedList)
            }
        }
        adapter.notifyDataSetChanged()
        cb_include_system_app.isEnabled = true
    }

    private fun searchAppList(content: String?, isCommit: Boolean = false) {
        content?.let { queryText ->
            if (queryText.isEmpty()) {
                setVisibility(SHOW_RV_CONTAINER)
                return
            }

            val lowerCaseQueryText = queryText.toLowerCase(Locale.getDefault())

            val results = data.filter {
                val lowerCaseName = it.name.toLowerCase(Locale.getDefault())
                val lowerCasePkg = it.pkg.toLowerCase(Locale.getDefault())
                //应用名称或包名含有查看字符
                lowerCaseName.contains(lowerCaseQueryText) || lowerCasePkg.contains(
                    lowerCaseQueryText
                )
            }
            Log.d(TAG, "searchTest...searchAppList()...results:$results")

            if (results.size in 1..10) {
                showSearchResults(results, content, isCommit)
            }
        }
    }

    private fun showSearchResults(results: List<AppItem>, keyWord: String?, isCommit: Boolean) {
        if (isCommit && results.isEmpty()) {
            Log.d(TAG, "searchTest...showSearchResults()...searchData empty")
            tv_state.text = "未找到相关结果"
            setVisibility(SHOW_TV_STATE)
            return
        }
        for (appItem in results) {
            appItem.keyWord = keyWord ?: ""
        }
        searchData.clear()
//        val sortedResult = results.sortedBy { it.name }
        searchData.addAll(results)
        Log.d(TAG, "searchTest...showSearchResults()...searchData:$searchData")
        searchResultsAdapter.notifyDataSetChanged()
        setVisibility(SHOW_RV_SEARCH_RESULTS_CONTAINER)
    }

    override fun onBackPressed() {
        if (rv_search_results_container.visibility == View.VISIBLE) {
            setVisibility(SHOW_RV_CONTAINER)
        } else {
            super.onBackPressed()
        }
    }

    private fun setVisibility(showWhich: Int) {
        tv_state.visibility = if (SHOW_TV_STATE == showWhich) View.VISIBLE else View.GONE
        rv_container.visibility = if (SHOW_RV_CONTAINER == showWhich) View.VISIBLE else View.GONE
        rv_search_results_container.visibility =
            if (SHOW_RV_SEARCH_RESULTS_CONTAINER == showWhich) View.VISIBLE else View.GONE
    }

    override fun onAppUninstall(uninstallPkg: String) {
        //移除动画有问题
        //remove from data list
        val iterator = data.listIterator()
        Log.d("主列表移除", "data：$data")
        while (iterator.hasNext()) {
            val index = iterator.nextIndex()
            val item = iterator.next()
            if (item.pkg == uninstallPkg) {
                Log.d("主列表移除", "remove index:：$index...item:$item")
                iterator.remove()
                adapter.notifyItemRemoved(index)
                Log.d("主列表移除", "remove range start:：$index...itemCount:${data.size - index}")
                adapter.notifyItemRangeChanged(index, data.size - index)
                break
            }
        }
        Log.d("主列表移除", "after remove:：$data")

        //remove from third party app list
        dataMap[TYPE_APP_THIRD]?.let {
            val thirdAppIterator = it.iterator()
            while (thirdAppIterator.hasNext()) {
                val item = thirdAppIterator.next()
                if (item.pkg == uninstallPkg) {
                    thirdAppIterator.remove()
                    break
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        uninstallReceiver?.let {
            unregisterReceiver(it)
        }
        loadAppListJob?.cancel()
    }
}