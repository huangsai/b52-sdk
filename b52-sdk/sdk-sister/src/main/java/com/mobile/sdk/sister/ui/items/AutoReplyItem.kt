package com.mobile.sdk.sister.ui.items

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.UnderlineSpan
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.http.ApiSysReply
import com.mobile.sdk.sister.databinding.SisterItemAutoReplyBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class AutoReplyItem(val data: ApiSysReply) : SimpleRecyclerItem() {

    private val underlineSpan = UnderlineSpan()

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(SisterItemAutoReplyBinding::bind)
        val spannableString = SpannableStringBuilder()
        val content = "${(holder.bindingAdapterPosition + 1)}.${data.words}"
        spannableString.append(content)
        spannableString.setSpan(
            underlineSpan,
            0,
            content.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        binding.itemAutoReply.text = spannableString
        holder.attachOnClickListener(R.id.item_auto_reply)
    }

    override fun getLayout(): Int {
        return R.layout.sister_item_auto_reply
    }
}