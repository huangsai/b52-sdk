package com.mobile.sdk.sister.ui.items

import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.http.ApiCharge
import com.mobile.sdk.sister.databinding.SisterItemSessionBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

/**
 * 客服会话列表
 */
class SessionItem(val data: ApiCharge) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(SisterItemSessionBinding::bind)
        holder.attachOnClickListener(R.id.item_session)
    }

    override fun getLayout(): Int {
        return R.layout.sister_item_session
    }
}