package com.floatingmuseum.app.analyser.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.floatingmuseum.app.analyser.entity.AppItem
import com.floatingmuseum.app.analyser.R
import com.floatingmuseum.app.analyser.ui.DetailActivity.Companion.DETAIL_PKG_NAME
import com.floatingmuseum.app.analyser.utils.*
import com.uber.autodispose.android.lifecycle.autoDispose
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val SHOW_TV_STATE = 0
        private const val SHOW_RV_CONTAINER = 1
        private const val SHOW_RV_SEARCH_RESULTS_CONTAINER = 2
    }

    private val data = mutableListOf<AppItem>()
    private val searchData = mutableListOf<AppItem>()
    private val dataMap = hashMapOf<Int, MutableList<AppItem>>()
    private lateinit var adapter: MainAdapter
    private lateinit var searchResultsAdapter: SearchResultsAdapter
    private var shouldContainsSystemApp = SPUtil.getBoolean(SP_KEY_CONTAINS_SYSTEM_APP, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        loadData()
    }

    private fun initView() {
        cb_include_system_app.isChecked = shouldContainsSystemApp
        adapter = MainAdapter(data)
        rv_container.adapter = adapter
        rv_container.layoutManager = LinearLayoutManager(this)
        rv_container.addOnItemTouchListener(object : OnItemClickListener() {
            override fun onSimpleItemClick(
                adapter: BaseQuickAdapter<*, *>?,
                view: View?,
                position: Int
            ) {
                Log.d("点击", "OnItemClickListener()...position:$position")
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DETAIL_PKG_NAME, data[position].pkg)
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
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DETAIL_PKG_NAME, searchData[position].pkg)
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

    private fun loadData() {
        Observable.create<Map<Int, MutableList<AppItem>>> {
            it.onNext(getAllAppMap())
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(this)
            .subscribe { result ->
                Log.d(TAG, "loadData()...data:$data")
                result[TYPE_APP_SYSTEM]?.let {
                    dataMap[TYPE_APP_SYSTEM] = it
                }
                result[TYPE_APP_THIRD]?.let {
                    dataMap[TYPE_APP_THIRD] = it
                }
                initHeaderView()
                updateAppList()
                ll_bottom_box.visibility = View.VISIBLE
                cb_include_system_app.isEnabled = true
                setVisibility(SHOW_RV_CONTAINER)
            }
    }

    private fun initHeaderView() {
        val headerView = LayoutInflater.from(this).inflate(R.layout.item_main_header, null)
        headerView.findViewById<TextView>(R.id.tv_about).setOnClickListener {
            startActivity(Intent(this@MainActivity, AboutActivity::class.java))
        }
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
}
