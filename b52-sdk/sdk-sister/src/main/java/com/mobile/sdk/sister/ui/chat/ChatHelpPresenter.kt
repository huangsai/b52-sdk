package com.mobile.sdk.sister.ui.chat

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.guava.android.mvvm.lifecycle.SimplePresenter
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.base.Msg
import com.mobile.sdk.sister.data.http.ApiHelp
import com.mobile.sdk.sister.data.http.ApiMessage
import com.mobile.sdk.sister.data.http.TYPE_TEXT
import com.mobile.sdk.sister.data.http.TYPE_TIME
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.SisterViewModel
import com.mobile.sdk.sister.ui.items.HelpItem
import com.mobile.sdk.sister.ui.items.MsgItem
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.RecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 快捷功能
 */
class ChatHelpPresenter(
    private val chatFragment: ChatFragment,
    private val binding: SisterFragmentChatBinding,
    private val model: SisterViewModel
) : SimplePresenter(), View.OnClickListener {

    private val adapter = RecyclerAdapter()

    init {
        binding.helpRecycler.layoutManager = LinearLayoutManager(
            chatFragment.requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.helpRecycler.addItemDecoration(
            LinearItemDecoration.builder(chatFragment.requireContext())
                .color(android.R.color.transparent, R.dimen.size_6dp)
                .horizontal()
                .build()
        )
        binding.helpRecycler.adapter = adapter
        adapter.onClickListener = this
    }

    fun load() {
        chatFragment.lifecycleScope.launch(Dispatchers.IO) {
            val items = listOf(
                ApiHelp(1, "充值方式"),
                ApiHelp(2, "游戏准入"),
                ApiHelp(3, "代理模式"),
                ApiHelp(4, "分享返利"),
            ).map {
                HelpItem(it)
            }
            withContext(Dispatchers.IO) {
                adapter.addAll(items)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.item_help_tag -> {
                val data = AdapterUtils.getHolder(v).item<HelpItem>().data
                sendMsg(data)
            }
        }
    }

    override fun onDestroyView() {
        binding.helpRecycler.adapter = null
    }

    private fun sendMsg(data: ApiHelp) {
        Msg.toast("发送快捷功能消息-->" + data.content)
    }
}