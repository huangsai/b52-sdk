package com.mobile.sdk.sister.ui.items

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.databinding.SisterItemViewerEmotionBinding
import com.mobile.sdk.sister.ui.chat.EmotionHandle
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import com.pacific.adapter.SimpleRecyclerItem


class ViewerEmotionItem(
    val data: List<EmotionHandle.EmotionInfo>,
    val binding: SisterFragmentChatBinding
) :
    SimpleRecyclerItem(), View.OnClickListener {

    private val adapter = RecyclerAdapter()

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(SisterItemViewerEmotionBinding::bind)
        val layoutManager = GridLayoutManager(holder.activity(), EmotionHandle.ROW_COUNT)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        val items = data.map { EmotionItem(it) }
        adapter.replaceAll(items)
        adapter.onClickListener = this
    }

    override fun getLayout(): Int {
        return R.layout.sister_item_viewer_emotion
    }

    override fun unbind(holder: AdapterViewHolder) {
        super.unbind(holder)
        val binding: SisterItemViewerEmotionBinding = holder.binding()
        binding.recyclerView.layoutManager = null
        binding.recyclerView.adapter = null
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.image_emotion -> {
                val data = AdapterUtils.getHolder(v).item<EmotionItem>().data
                val spannable = EmotionHandle.buildEmotionSpannable(
                    data.textContent,
                    binding.chatEt.textSize.toInt()
                )
                val start = binding.chatEt.selectionStart
                val editable = binding.chatEt.editableText
                editable?.insert(start, spannable)
            }
        }
    }

}