package com.mobile.sdk.sister.ui.chat

import android.view.View
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.guava.android.mvvm.Msg
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.mobile.guava.jvm.domain.Source
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.http.TYPE_TEXT_A
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.SisterViewModel
import com.mobile.sdk.sister.ui.items.HelpItem
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatHelpPresenter(
    fragment: ChatFragment,
    binding: SisterFragmentChatBinding,
    model: SisterViewModel
) : BaseChatPresenter(fragment, binding, model) {

    private val adapter = RecyclerAdapter()

    init {
        binding.helpRecycler.layoutManager = LinearLayoutManager(
            fragment.requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.helpRecycler.addItemDecoration(
            LinearItemDecoration.builder(fragment.requireContext())
                .color(android.R.color.transparent, R.dimen.size_6dp)
                .horizontal()
                .build()
        )
        binding.helpRecycler.adapter = adapter
        adapter.onClickListener = this
    }

    override fun load() {
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            val source = model.getSysReply()
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        val items = source.requireData().map {
                            HelpItem(it)
                        }
                        adapter.addAll(items)
                    }
                    is Source.Error -> {
                        Msg.toast(source.requireError().message!!)
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.item_help_tag -> {
                val data = AdapterUtils.getHolder(v).item<HelpItem>().data
                fragment.chatListPresenter.postText(data.words, TYPE_TEXT_A)
            }
        }
    }

    override fun onDestroyView() {
        binding.helpRecycler.adapter = null
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        TODO("Not yet implemented")
    }
}