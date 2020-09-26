package com.mobile.sdk.sister.ui.items

import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterItemEmotionBinding
import com.mobile.sdk.sister.ui.chat.EmotionHandle
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class EmotionItem(val data: EmotionHandle.EmotionInfo) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(SisterItemEmotionBinding::bind)
        binding.imageEmotion.setImageResource(data.drawableId)
        holder.attachOnClickListener(R.id.image_emotion)
    }

    override fun getLayout(): Int {
        return R.layout.sister_item_emotion
    }
}