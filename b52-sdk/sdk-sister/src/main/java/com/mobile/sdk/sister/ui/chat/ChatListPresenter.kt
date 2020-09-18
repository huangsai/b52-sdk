package com.mobile.sdk.sister.ui.chat

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.guava.android.mvvm.Msg
import com.mobile.guava.android.mvvm.lifecycle.SimplePresenter
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.mobile.guava.android.ui.view.recyclerview.keepItemViewVisible
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.file.AppPreferences
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.SisterViewModel
import com.mobile.sdk.sister.ui.items.MsgItem
import com.pacific.adapter.AdapterImageLoader
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 聊天列表
 */
class ChatListPresenter(
    private val chatFragment: ChatFragment,
    private val binding: SisterFragmentChatBinding,
    private val model: SisterViewModel
) : SimplePresenter(), View.OnClickListener, AdapterImageLoader, TextView.OnEditorActionListener {

    private val adapter = RecyclerAdapter()

    init {
        binding.chatRecycler.layoutManager = LinearLayoutManager(chatFragment.requireContext())
        binding.chatRecycler.addItemDecoration(
            LinearItemDecoration.builder(chatFragment.requireContext())
                .color(android.R.color.transparent, R.dimen.size_10dp)
                .build()
        )
        adapter.onClickListener = this
        adapter.imageLoader = this
        binding.chatRecycler.adapter = adapter

        binding.chatEt.setOnEditorActionListener(this)
    }

    fun load() {
        chatFragment.lifecycleScope.launch(Dispatchers.IO) {
            val userId = AppPreferences.userId
            val items = model.loadMessages().map {
                MsgItem.create(it, userId)
            }
            withContext(Dispatchers.Main) {
                adapter.addAll(items)
            }
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.profile -> {
                //TODO 点击头像
                Msg.toast("点击头像")
            }
            R.id.image_content -> {
                //TODO 点击图片
                Msg.toast("点击图片")
            }
            R.id.audio_content -> {
                //TODO 点击语音
                Msg.toast("点击语音")
            }
            R.id.deposit_wechat -> {
                //TODO 点击微信充值
                Msg.toast("点击微信充值")
            }
            R.id.deposit_alipay -> {
                //TODO 点击支付宝充值
                Msg.toast("点击支付宝充值")
            }
            R.id.status -> {
                //TODO 失败，点击重新发送消息
                val item = AdapterUtils.getHolder(v).item<MsgItem>()
                val content = item.data.content
                Msg.toast("重新发送消息${content}")
            }
        }
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        when (view.id) {
            R.id.profile -> {
                //TODO 加载头像
            }
            R.id.image_content -> {
                //TODO 加载图片内容
            }
        }
    }

    override fun onDestroyView() {
        binding.chatRecycler.adapter = null
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            postText(v.text.toString())
        }
        return false
    }

    private fun postText(text: String) {
        chatFragment.lifecycleScope.launch(Dispatchers.IO) {
            val item = MsgItem.create(
                model.postText(text, 0L),
                AppPreferences.userId
            )
            withContext(Dispatchers.Main) {
                adapter.add(item)
                binding.chatEt.setText("")
                binding.chatRecycler.keepItemViewVisible(adapter.indexOf(item))
            }
        }
    }
}