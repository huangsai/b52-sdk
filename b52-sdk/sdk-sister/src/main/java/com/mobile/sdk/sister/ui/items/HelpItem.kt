package com.mobile.sdk.sister.ui.items

import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.http.ApiHelp
import com.mobile.sdk.sister.databinding.SisterItemHelpBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class HelpItem(val data: ApiHelp) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(SisterItemHelpBinding::bind)
        binding.itemHelpTag.text = data.content
        holder.attachOnClickListener(R.id.item_help_tag)
    }

    override fun getLayout(): Int {
        return R.layout.sister_item_help
    }
}