package com.mobile.sdk.sister.ui.items

import androidx.recyclerview.widget.GridLayoutManager
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterItemViewerEmojiBinding
import com.mobile.sdk.sister.ui.EmojiHandle
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import com.pacific.adapter.SimpleRecyclerItem

class ViewerEmojiItem(val data: List<EmojiHandle.EmojiInfo>) : SimpleRecyclerItem() {

    private val adapter = RecyclerAdapter()
    private val rowCount = 9

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(SisterItemViewerEmojiBinding::bind)
        val layoutManager = GridLayoutManager(holder.activity(), rowCount)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        val items = data.map { EmojiItem(it) }
        adapter.addAll(items)
    }

    override fun getLayout(): Int {
        return R.layout.sister_item_viewer_emoji
    }

    override fun unbind(holder: AdapterViewHolder) {
        super.unbind(holder)
        val binding: SisterItemViewerEmojiBinding = holder.binding()
        binding.recyclerView.layoutManager = null
        binding.recyclerView.adapter = null
    }

}