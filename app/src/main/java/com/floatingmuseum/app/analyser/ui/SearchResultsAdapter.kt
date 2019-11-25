package com.floatingmuseum.app.analyser.ui

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.floatingmuseum.app.analyser.entity.AppItem
import com.floatingmuseum.app.analyser.R
import java.util.*

/**
 * Created by Floatingmuseum on 2019-10-15.
 */
class SearchResultsAdapter(data: MutableList<AppItem>) :
    BaseQuickAdapter<AppItem, BaseViewHolder>(R.layout.item_app, data) {

    override fun convert(helper: BaseViewHolder, item: AppItem) {
        Log.d("searchTest", "searchTest...adapter...convert()...item:$item")
        val backgroundId =
            if (item.isSystem) R.drawable.dash_line_red_background else R.drawable.dash_line_green_background
        helper.setText(R.id.tv_ver_name, "版本:${item.verName}")
            .setImageDrawable(R.id.iv_icon, item.icon)
            .setBackgroundRes(R.id.ll_item_app, backgroundId)

        val lowerCaseKey = item.keyWord.toLowerCase(Locale.getDefault())
        if (item.name.toLowerCase(Locale.getDefault()).contains(lowerCaseKey)) {
            val spanName = SpannableString("名称:${item.name}")
            val start = spanName.indexOf(lowerCaseKey, 3, true)
            val end = start + lowerCaseKey.length
            spanName.setSpan(
                ForegroundColorSpan(Color.RED),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            helper.setText(R.id.tv_name, spanName)
        } else {
            helper.setText(R.id.tv_name, "名称:${item.name}")
        }
        if (item.pkg.toLowerCase(Locale.getDefault()).contains(lowerCaseKey)) {
            val spanPkg = SpannableString("包名:${item.pkg}")
            val start = spanPkg.indexOf(lowerCaseKey, 3, true)
            val end = start + lowerCaseKey.length
            spanPkg.setSpan(
                ForegroundColorSpan(Color.RED),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            helper.setText(R.id.tv_package_name, spanPkg)
        } else {
            helper.setText(R.id.tv_package_name, "包名:${item.pkg}")
        }
    }
}