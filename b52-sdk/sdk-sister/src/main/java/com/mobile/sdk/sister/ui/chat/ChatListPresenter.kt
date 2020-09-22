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
                .color(android.R.color.transparent, R.dimen.size_10dp)
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
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            val items = model.loadMessages().map {
                MsgItem.create(it)
            }
            withContext(Dispatchers.Main) {
                adapter.addAll(items)
                binding.chatRecycler.keepItemViewVisible(items.size - 1, false)
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
                TYPE_AUDIO -> model.postAudio(dbMessage)
                else -> {
                }
            }
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
            mMediaPlayer.prepareAsync()
        } catch (e: Exception) {
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        isAudioPlaying = true
        onAudioStatusChanged()
        mp?.start()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        isAudioPlaying = false
        onAudioStatusChanged()
        mp?.reset()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        isAudioPlaying = false
        onAudioStatusChanged()
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