package com.mobile.sdk.sister.ui.chat

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.guava.android.mvvm.lifecycle.SimplePresenter
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.http.*
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.SisterViewModel
import com.mobile.sdk.sister.ui.items.MsgItem
import com.pacific.adapter.RecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 聊天列表
 */
class ChatListPresenter(
    private val chatFragment: ChatFragment,
    private val binding: SisterFragmentChatBinding,
    private val model: SisterViewModel
) : SimplePresenter() {

    private val adapter = RecyclerAdapter()

    init {
        binding.chatRecycler.layoutManager = LinearLayoutManager(chatFragment.requireContext())
        binding.chatRecycler.addItemDecoration(
            LinearItemDecoration.builder(chatFragment.requireContext())
                .color(android.R.color.transparent, R.dimen.size_10dp)
                .build()
        )
        binding.chatRecycler.adapter = adapter
    }

    fun load() {
        chatFragment.lifecycleScope.launch(Dispatchers.IO) {
            val items = model.loadMessages().map {
                MsgItem.create(it)
            }
            withContext(Dispatchers.IO) {
                adapter.addAll(items)
            }
        }
    }

    override fun onDestroyView() {
        binding.chatRecycler.adapter = null
    }
}