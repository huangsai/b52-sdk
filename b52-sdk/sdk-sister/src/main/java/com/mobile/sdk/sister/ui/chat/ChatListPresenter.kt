package com.mobile.sdk.sister.ui.chat

import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_MUSIC
import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ext.glide.GlideApp
import com.mobile.guava.android.context.hideSoftInput
import com.mobile.guava.android.mvvm.Msg
import com.mobile.guava.android.mvvm.showDialogFragment
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.mobile.guava.android.ui.view.recyclerview.keepItemViewVisible
import com.mobile.guava.jvm.coroutines.Bus
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.http.*
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.socket.SocketUtils
import com.mobile.sdk.sister.ui.*
import com.mobile.sdk.sister.ui.items.MsgItem
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

class ChatListPresenter(
    fragment: TopMainFragment,
    binding: SisterFragmentChatBinding,
    model: SisterViewModel,
    private val isCharge: Boolean
) : BaseChatPresenter(fragment, binding, model), MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private val adapter = RecyclerAdapter()
    private var mMediaPlayer = MediaPlayer()
    private var curPlayingPosition = -1

    private val scrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (adapter.itemCount == 0 || binding.txtNewest.isInvisible) {
                    return
                }
                binding.chatRecycler.layoutManager?.let { layoutManager ->
                    val linearLayoutManager = LinearLayoutManager::class.java.cast(layoutManager)!!
                    val lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()
                    if (layoutManager.itemCount - lastVisibleItemPosition == 1) {
                        binding.txtNewest.isInvisible = true
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            }
        }

    init {
        binding.txtNewest.setOnClickListener(this)
        binding.chatRecycler.layoutManager = LinearLayoutManager(fragment.requireContext())
        binding.chatRecycler.addItemDecoration(
            LinearItemDecoration.builder(fragment.requireContext())
                .color(android.R.color.transparent, R.dimen.size_8dp)
                .build()
        )
        binding.chatRecycler.addOnScrollListener(scrollListener)
        adapter.onClickListener = this
        adapter.imageLoader = this
        binding.chatRecycler.adapter = adapter

        mMediaPlayer.setOnPreparedListener(this)
        mMediaPlayer.setOnCompletionListener(this)
        mMediaPlayer.setOnErrorListener(this)
        mMediaPlayer.setAudioAttributes(
            AudioAttributes.Builder().setContentType(CONTENT_TYPE_MUSIC).build()
        )
    }

    override fun onDestroyView() {
        binding.chatRecycler.removeOnScrollListener(scrollListener)
        binding.chatRecycler.adapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer.release()
    }

    override fun load() {
        if (!adapter.isEmpty()) {
            return
        }

        if (isCharge) {
            requestChargeMsg()
        } else {
            requestSisterMsg()
        }
    }

    /**
     * 请求充值会话聊天记录
     */
    private fun requestChargeMsg() {

    }

    /**
     * 请求客服会话聊天记录
     */
    private fun requestSisterMsg() {
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            val sourceItems = model.loadMessages().map {
                MsgItem.create(it)
            }

            withContext(Dispatchers.Main) {
                adapter.replaceAll(sourceItems.plus(SisterX.bufferMsgItems))
                SisterX.bufferMsgItems.clear()
                SisterX.hasBufferMsgItems.value = false
                SisterX.isUiPrepared.value = true
                scrollToBottom()
            }
        }
    }

    private fun scrollToBottom() {
        binding.chatRecycler.postDelayed(
            {
                binding.chatRecycler.keepItemViewVisible(
                    adapter.itemCount - 1,
                    true
                )
            },
            300
        )
    }

    fun cleanMessages() {
        adapter.clear()
    }

    fun onMessageStatusChanged(dbMessage: DbMessage) {
        adapter.getAll()
            .filterIsInstance<MsgItem>()
            .filter { it.data.id == dbMessage.id }
            .forEach {
                it.data.status = dbMessage.status
                adapter.notifyItemChanged(adapter.indexOf(it), SisterX.BUS_MSG_STATUS)
            }
    }

    fun onNewMessage(msgItem: MsgItem) {
        if (binding.chatRecycler.canScrollVertically(1)) {
            adapter.add(msgItem)
            binding.txtNewest.isInvisible = false
        } else {
            addMsgItem(msgItem)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.txt_newest -> {
                binding.chatRecycler.scrollToPosition(adapter.itemCount - 1)
            }
            R.id.profile -> {
                Msg.toast("点击头像")
            }
            R.id.image_content -> {
                val holder = AdapterUtils.getHolder(v)
                val list = getImageUrls()
                fragment.showDialogFragment(
                    ImageViewerDialogFragment.newInstance(
                        list.map { it.url.toUri() },
                        list.indexOf(holder.item<MsgItem>().image)
                    )
                )
            }
            R.id.layout_audio -> {
                clickAudio(AdapterUtils.getHolder(v))
            }
            R.id.deposit_wechat -> {
                if (isCharge)
                    Bus.offer(SisterX.BUS_CLICK_WECHAT)
            }
            R.id.deposit_alipay -> {
                if (isCharge)
                    Bus.offer(SisterX.BUS_CLICK_ALIPAY)
            }
            R.id.deposit_union -> {
                if (isCharge)
                    Bus.offer(SisterX.BUS_CLICK_UNION)
            }
            R.id.status_failed -> {
                retryPostMsg(AdapterUtils.getHolder(v).item<MsgItem>().data)
            }
            R.id.leave_msg_click -> {
                ChatLeaveMsgPresenter(fragment, binding, model).showPop()
            }
            R.id.auto_reply_click -> {
                model.requestSister()
            }
        }
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        when (view.id) {
            R.id.profile -> {
                GlideApp.with(fragment)
                    .load(holder.item<MsgItem>().data.fromUserImage)
                    .placeholder(R.drawable.sister_default_profile)
                    .into(view)
            }
            R.id.image_content -> {
                val url = holder.item<MsgItem>().image.url
                GlideApp.with(fragment)
                    .load(url.toUri())
                    .error(R.drawable.sister_default_img)
                    .into(view)
            }
        }
    }

    fun postText(text: String) {
        if (text.isNullOrEmpty()) {
            Msg.toast(R.string.sister_msg_content_empty_toast)
            return
        }
        fragment.hideSoftInput()
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            val dbMessage = model.createDbMessage(TYPE_TEXT, DbMessage.Text(text).toJson())
            val item = MsgItem.create(dbMessage)
            withContext(Dispatchers.Main) {
                addMsgItem(item)
                binding.chatEt.setText("")
            }
            model.postText(dbMessage)
        }
    }

    //TODO 测试
    fun postChargeText(text: String) {
        postText(text)
        val type = when (text) {
            "微信" -> TYPE_DEPOSIT_WECHAT
            "支付宝" -> TYPE_DEPOSIT_ALIPAY
            else -> TYPE_DEPOSIT_UNION
        }
        SocketUtils.insertDbMessage(
            DbMessage.Deposit(
                "",
                type,
                ""
            ).sisterDepositDbMessage()
        )
    }

    fun postImage(uri: Uri) {
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            val dbMessage = model.createDbMessage(
                TYPE_IMAGE,
                DbMessage.Image(uri.toString()).toJson()
            )
            val item = MsgItem.create(dbMessage)
            withContext(Dispatchers.Main) {
                addMsgItem(item)
            }
            model.postImage(dbMessage)
        }
    }

    fun postAudio(duration: Long, audio: File) {
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            val dbMessage = model.createDbMessage(
                TYPE_AUDIO,
                DbMessage.Audio(duration, audio.path).toJson()
            )
            val item = MsgItem.create(dbMessage)
            withContext(Dispatchers.Main) {
                addMsgItem(item)
            }
            model.postAudio(dbMessage)
        }
    }

    private fun addMsgItem(item: MsgItem) {
        if (adapter.isEmpty()) {
            adapter.add(MsgItem.create(item.data.crossTime()))
        } else {
            val lastItem = adapter.get<MsgItem>(adapter.itemCount - 1)
            if (item.data.time - lastItem.data.time >= MSG_TIME_DIFF) {
                adapter.add(MsgItem.create(item.data.crossTime()))
            }
        }
        adapter.add(item)
        scrollToBottom()
    }

    private fun retryPostMsg(dbMessage: DbMessage) {
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            when (dbMessage.type) {
                TYPE_TEXT -> model.postText(dbMessage)
                TYPE_IMAGE -> model.postImage(dbMessage)
                TYPE_AUDIO -> model.postAudio(dbMessage)
                else -> {
                }
            }
        }
    }

    private fun clickAudio(holder: AdapterViewHolder) {
        val position = holder.bindingAdapterPosition
        val item = holder.item<MsgItem>()

        // 刷新点击的语音条目动画
        item.isAudioPlaying = !item.isAudioPlaying
        adapter.notifyItemChanged(position, SisterX.BUS_MSG_AUDIO_PLAYING)

        // 如果当前正在播放的语音条目不是点击条目
        if (curPlayingPosition != position) {
            curMsgAudioItem()?.let {
                // 刷新当前正在播放的语音动画
                it.isAudioPlaying = !it.isAudioPlaying
                adapter.notifyItemChanged(curPlayingPosition, SisterX.BUS_MSG_AUDIO_PLAYING)
            }
            curPlayingPosition = position
        }
        // 重置播放器
        mMediaPlayer.reset()
        if (item.isAudioPlaying) {
            try {
                mMediaPlayer.setDataSource(item.audio.url)
                mMediaPlayer.prepareAsync()
            } catch (e: Exception) {
                Timber.d(e)
            }
        } else {
            curPlayingPosition = -1
        }
    }

    // 关闭语音播放动画
    private fun isNotAudioPlaying() {
        curMsgAudioItem()?.let {
            it.isAudioPlaying = false
            adapter.notifyItemChanged(curPlayingPosition, SisterX.BUS_MSG_AUDIO_PLAYING)
            curPlayingPosition = -1
        }
    }

    private fun curMsgAudioItem(): MsgItem? {
        return if (curPlayingPosition == -1) null else adapter.get(curPlayingPosition)
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        isNotAudioPlaying()
        mp?.reset()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Msg.toast(R.string.sister_audio_play_error_toast)
        isNotAudioPlaying()
        mp?.reset()
        return false
    }

    private fun getImageUrls(): List<DbMessage.Image> {
        return adapter.getAll()
            .filterIsInstance<MsgItem>()
            .filter { it is MsgItem.Image || it is MsgItem.Image2 }
            .map { it.image }
    }
}