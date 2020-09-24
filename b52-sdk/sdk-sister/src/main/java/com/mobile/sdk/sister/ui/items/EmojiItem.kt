package com.mobile.sdk.sister.ui.items

import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterItemEmojiBinding
import com.mobile.sdk.sister.ui.EmojiHandle
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class EmojiItem(val data: EmojiHandle.EmojiInfo) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(SisterItemEmojiBinding::bind)
        binding.imageEmoji.setImageResource(data.drawableId)
    }

    override fun getLayout(): Int {
        return R.layout.sister_item_emoji
    }
}