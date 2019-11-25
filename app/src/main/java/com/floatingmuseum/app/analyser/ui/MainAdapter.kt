package com.floatingmuseum.app.analyser.ui

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.floatingmuseum.app.analyser.entity.AppItem
import com.floatingmuseum.app.analyser.R

/**
 * Created by Floatingmuseum on 2019-10-03.
 */
class MainAdapter(data: MutableList<AppItem>) :
    BaseQuickAdapter<AppItem, BaseViewHolder>(R.layout.item_app, data) {

    override fun convert(helper: BaseViewHolder, item: AppItem) {
        helper.setText(R.id.tv_name, "名称:${item.name}")
            .setText(R.id.tv_package_name, "包名:${item.pkg}")
            .setText(R.id.tv_ver_name, "版本:${item.verName}")
            .setImageDrawable(R.id.iv_icon, item.icon)
            .setBackgroundRes(
                R.id.ll_item_app,
                if (item.isSystem) R.drawable.dash_line_red_background else R.drawable.dash_line_green_background
            )
    }
}