package com.mobile.sdk.sister.ui.chat

import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_MUSIC
import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ext.glide.GlideApp
import com.mobile.guava.android.mvvm.Msg
import com.mobile.guava.android.mvvm.showDialogFragment
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.mobile.guava.android.ui.view.recyclerview.keepItemViewVisible
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.http.TYPE_AUDIO
import com.mobile.sdk.sister.data.http.TYPE_IMAGE
import com.mobile.sdk.sister.data.http.TYPE_TEXT
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.SisterViewModel
import com.mobile.sdk.sister.ui.items.MsgItem
import com.mobile.sdk.sister.ui.jsonToAudio
import com.mobile.sdk.sister.ui.toJson
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ChatListPresenter(
    fragment: ChatFragment,
    binding: SisterFragmentChatBinding,
    model: SisterViewModel
) : BaseChatPresenter(fragment, binding, model), MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private val adapter = RecyclerAdapter()
    private var mMediaPlayer = MediaPlayer()
    private var currentPlayAudioDBMsg: DbMessage? = null
    private var isAudioPlaying = false

    init {
        binding.chatRecycler.layoutManager = LinearLayoutManager(fragment.requireContext())
        binding.chatRecycler.addItemDecoration(
            LinearItemDecoration.builder(fragment.requireContext())
                .color(android.R.color.transparent, R.dimen.size_10dp)
                .build()
        )
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
        binding.chatRecycler.adapter = null
    }

    override fun load() {
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            val items = model.loadMessages().map {
                MsgItem.create(it)
            }
            withContext(Dispatchers.Main) {
                adapter.addAll(items)
                binding.chatRecycler.keepItemViewVisible(adapter.itemCount - 1, true)
            }
        }
    }

    fun onMessageStatusChanged(dbMessage: DbMessage) {
        adapter.getAll()
            .filterIsInstance<MsgItem>()
            .first { it.data.id == dbMessage.id }
            .let {
                it.data.status = dbMessage.status
                adapter.notifyItemChanged(adapter.indexOf(it))
            }
    }

    fun onNewMessage(msgItem: MsgItem) {
        adapter.add(msgItem)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
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
                val data = AdapterUtils.getHolder(v).item<MsgItem>().data
                clickAudio(data)
            }
            R.id.deposit_wechat -> {
                Msg.toast("点击微信充值")
            }
            R.id.deposit_alipay -> {
                Msg.toast("点击支付宝充值")
            }
            R.id.status_failed -> retryPostMsg(AdapterUtils.getHolder(v).item<MsgItem>().data)
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
            Msg.toast("不能发送空消息")
            return
        }
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            val dbMessage = model.createDbMessage(
                TYPE_TEXT,
                DbMessage.Text(text).toJson()
            )
            val item = MsgItem.create(dbMessage)
            withContext(Dispatchers.Main) {
                addMsgItem(item)
                binding.chatEt.setText("")
            }
            model.postText(dbMessage)
        }
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
        adapter.add(item)
        binding.chatRecycler.keepItemViewVisible(adapter.itemCount - 1, true)
    }

    private fun retryPostMsg(dbMessage: DbMessage) {
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            when (dbMessage.type) {
                TYPE_TEXT -> model.postText(dbMessage)
                TYPE_IMAGE -> model.postImage(dbMessage)
                TYPE_AUDIO -> model.postImage(dbMessage)
                else -> {
                }
            }
        }
    }

    private fun clickAudio(data: DbMessage) {
        if (isAudioPlaying) {
            mMediaPlayer.reset()
            if (currentPlayAudioDBMsg?.id == data.id) { //点击的语音是当前正在播放的语音
                isAudioPlaying = false
                onAudioStatusChanged()
                return
            }
        }
        try {
            currentPlayAudioDBMsg = data
            mMediaPlayer.setDataSource(data.content.jsonToAudio().url)
            mMediaPlayer.prepare()
            mMediaPlayer.start()
        } catch (e: Exception) {
        }
    }

    private fun onAudioStatusChanged() {
        adapter.getAll()
            .filterIsInstance<MsgItem>()
            .filter {
                it is MsgItem.Audio || it is MsgItem.Audio2
            }
            .forEach {
                if (it.data.id == currentPlayAudioDBMsg?.id) {
                    it.audio.isPlaying = isAudioPlaying
                } else {
                    it.audio.isPlaying = false
                }
                adapter.notifyItemChanged(adapter.indexOf(it))
            }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        isAudioPlaying = true
        onAudioStatusChanged()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        mp?.reset()
        isAudioPlaying = false
        onAudioStatusChanged()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        isAudioPlaying = false
        mMediaPlayer.reset()
        onAudioStatusChanged()
        return false
    }

    private fun getImageUrls(): List<DbMessage.Image> {
        return adapter.getAll()
            .filterIsInstance<MsgItem>()
            .filter { it is MsgItem.Image || it is MsgItem.Image2 }
            .map { it.image }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer.release()
    }


}