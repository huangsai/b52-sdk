package com.mobile.sdk.sister.ui.items

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import com.mobile.guava.android.ui.view.text.MySpannable
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.http.ApiSysReply
import com.mobile.sdk.sister.databinding.SisterItemAutoReplyBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class AutoReplyItem(val data: ApiSysReply) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(SisterItemAutoReplyBinding::bind)
        binding.itemAutoReply.text = MySpannable(
            "${(holder.bindingAdapterPosition + 1)}.${data.question}",
            UnderlineSpan(),
            ForegroundColorSpan(Color.parseColor("#2271e6"))
        )
        holder.attachOnClickListener(R.id.item_auto_reply)
    }

    override fun getLayout(): Int {
        return R.layout.sister_item_auto_reply
    }
}