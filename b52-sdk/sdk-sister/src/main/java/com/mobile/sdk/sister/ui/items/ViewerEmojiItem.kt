package com.mobile.sdk.sister.ui.items

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.databinding.SisterItemViewerEmojiBinding
import com.mobile.sdk.sister.ui.EmojiHandle
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import com.pacific.adapter.SimpleRecyclerItem

class ViewerEmojiItem(
    val data: List<EmojiHandle.EmojiInfo>,
    val binding: SisterFragmentChatBinding
) :
    SimpleRecyclerItem(), View.OnClickListener {

    private val adapter = RecyclerAdapter()

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(SisterItemViewerEmojiBinding::bind)
        val layoutManager = GridLayoutManager(holder.activity(), EmojiHandle.ROW_COUNT)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        val items = data.map { EmojiItem(it) }
        adapter.replaceAll(items)
        adapter.onClickListener = this
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.image_emoji -> {
                val data = AdapterUtils.getHolder(v).item<EmojiItem>().data
                binding.chatEt.append(data.textContent)
            }
        }
    }

}