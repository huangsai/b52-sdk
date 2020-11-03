package com.mobile.sdk.sister.ui.items

import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.http.ApiSysReply
import com.mobile.sdk.sister.databinding.SisterItemAutoReplyBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class AutoReplyItem(val data: ApiSysReply) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(SisterItemAutoReplyBinding::bind)
        binding.itemAutoReply.text = data.words
        holder.attachOnClickListener(R.id.item_help_tag)
    }

    override fun getLayout(): Int {
        return R.layout.sister_item_auto_reply
    }
}