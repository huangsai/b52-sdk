package com.mobile.sdk.sister.ui.items

import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.http.ApiNotice
import com.mobile.sdk.sister.databinding.SisterItemSystemNoticeBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class NoticeItem(val data: ApiNotice) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(SisterItemSystemNoticeBinding::bind)
        binding.systemContent.text = data.content
    }

    override fun getLayout(): Int {
        return R.layout.sister_item_system_notice
    }
}