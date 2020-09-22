package com.mobile.sdk.sister.ui.items

import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import com.mobile.guava.jvm.date.yyyy_mm_dd_hh_mm_ss
import com.mobile.guava.jvm.extension.cast
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.file.AppPreferences
import com.mobile.sdk.sister.data.http.*
import com.mobile.sdk.sister.databinding.*
import com.mobile.sdk.sister.ui.*
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

abstract class MsgItem(val data: DbMessage) : SimpleRecyclerItem() {

    lateinit var text: DbMessage.Text
        private set

    lateinit var audio: DbMessage.Audio
        private set

    lateinit var image: DbMessage.Image
        private set

    lateinit var deposit: DbMessage.Deposit
        private set

    lateinit var time: DbMessage.Time
        private set

    lateinit var system: DbMessage.System
        private set

    lateinit var upgrade: DbMessage.Upgrade
        private set

    var isAudioPlaying: Boolean = false

    internal fun ofText(): MsgItem = apply { text = data.content.jsonToText() }

    internal fun ofImage(): MsgItem = apply { image = data.content.jsonToImage() }

    internal fun ofAudio(): MsgItem = apply { audio = data.content.jsonToAudio() }

    internal fun ofDeposit(): MsgItem = apply { deposit = data.content.jsonToDeposit() }

    internal fun ofTime(): MsgItem = apply { time = data.content.jsonToTime() }

    internal fun ofSystem(): MsgItem = apply { system = data.content.jsonToSystem() }

    internal fun ofUpgrade(): MsgItem = apply { upgrade = data.content.jsonToUpgrade() }

    protected fun setStatus(statusFailed: View, statusProcessing: View) {
        when (data.status) {
            STATUS_MSG_PROCESSING -> {
                statusProcessing.isVisible = true
                statusFailed.isVisible = false
            }
            STATUS_MSG_FAILED -> {
                statusProcessing.isVisible = false
                statusFailed.isVisible = true
            }
            else -> {
                statusProcessing.isVisible = false
                statusFailed.isVisible = false
            }
        }
    }

    protected fun applyIsAudioPlay(view: ImageView) {
        if (isAudioPlaying) {
            view.drawable.cast<AnimationDrawable>().start()
        } else {
            view.drawable.cast<AnimationDrawable>().stop()
        }
    }

    class Text(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatToTextBinding::bind)
            binding.textContent.text = text.msg
            setStatus(binding.statusFailed, binding.statusProcessing)
            holder.attachOnClickListener(R.id.status_failed)
            holder.attachImageLoader(R.id.profile)
            holder.attachOnClickListener(R.id.profile)
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_to_text
        }

        override fun bindPayloads(holder: AdapterViewHolder, payloads: List<Any>?) {
            if (payloads.isNullOrEmpty()) return
            val event = payloads[0] as Int

            val binding: SisterItemChatToTextBinding = holder.binding()
            if (event == SisterX.BUS_MSG_STATUS) {
                setStatus(binding.statusFailed, binding.statusProcessing)
            }
        }
    }

    class Image(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatToImageBinding::bind)
            setStatus(binding.statusFailed, binding.statusProcessing)
            holder.attachOnClickListener(R.id.status_failed)
            holder.attachImageLoader(R.id.profile)
            holder.attachOnClickListener(R.id.profile)
            holder.attachImageLoader(R.id.image_content)
            holder.attachOnClickListener(R.id.image_content)
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_to_image
        }

        override fun bindPayloads(holder: AdapterViewHolder, payloads: List<Any>?) {
            if (payloads.isNullOrEmpty()) return
            val event = payloads[0] as Int

            val binding: SisterItemChatToImageBinding = holder.binding()
            if (event == SisterX.BUS_MSG_STATUS) {
                setStatus(binding.statusFailed, binding.statusProcessing)
            }
        }
    }

    class Audio(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatToAudioBinding::bind)
            binding.audioDuration.text = (audio.duration / 1000).toInt().toAudioText()
            setStatus(binding.statusFailed, binding.statusProcessing)
            applyIsAudioPlay(binding.audioImg)

            holder.attachOnClickListener(R.id.status_failed)
            holder.attachImageLoader(R.id.profile)
            holder.attachOnClickListener(R.id.profile)
            holder.attachOnClickListener(R.id.layout_audio)
        }

        override fun bindPayloads(holder: AdapterViewHolder, payloads: List<Any>?) {
            if (payloads.isNullOrEmpty()) return
            val event = payloads[0] as Int

            val binding: SisterItemChatToAudioBinding = holder.binding()
            if (event == SisterX.BUS_MSG_STATUS) {
                setStatus(binding.statusFailed, binding.statusProcessing)
                return
            }

            if (event == SisterX.BUS_MSG_AUDIO_PLAYING) {
                applyIsAudioPlay(binding.audioImg)
                return
            }
        }

        override fun unbind(holder: AdapterViewHolder) {
            val binding: SisterItemChatToAudioBinding = holder.binding()
            binding.audioImg.drawable.cast<AnimationDrawable>().stop()
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_to_audio
        }
    }

    class Text2(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatFromTextBinding::bind)
            binding.textContent.text = text.msg
            holder.attachImageLoader(R.id.profile)
            holder.attachOnClickListener(R.id.profile)
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_from_text
        }
    }

    class Image2(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatFromImageBinding::bind)
            holder.attachImageLoader(R.id.profile)
            holder.attachOnClickListener(R.id.profile)
            holder.attachImageLoader(R.id.image_content)
            holder.attachOnClickListener(R.id.image_content)
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_from_image
        }
    }

    class Audio2(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatFromAudioBinding::bind)
            binding.audioDuration.text = (audio.duration / 1000).toInt().toAudio2Text()
            applyIsAudioPlay(binding.audioImg)
            holder.attachImageLoader(R.id.profile)
            holder.attachOnClickListener(R.id.profile)
            holder.attachOnClickListener(R.id.layout_audio)
        }

        override fun bindPayloads(holder: AdapterViewHolder, payloads: List<Any>?) {
            if (payloads.isNullOrEmpty()) return
            val event = payloads[0] as Int

            val binding: SisterItemChatFromAudioBinding = holder.binding()
            if (event == SisterX.BUS_MSG_AUDIO_PLAYING) {
                applyIsAudioPlay(binding.audioImg)
                return
            }
        }

        override fun unbind(holder: AdapterViewHolder) {
            val binding: SisterItemChatFromAudioBinding = holder.binding()
            binding.audioImg.drawable.cast<AnimationDrawable>().stop()
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_from_audio
        }
    }

    class Upgrade(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            TODO("Not yet implemented")
        }

        override fun getLayout(): Int {
            TODO("Not yet implemented")
        }
    }

    class Upgrade2(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            TODO("Not yet implemented")
        }

        override fun getLayout(): Int {
            TODO("Not yet implemented")
        }
    }

    class Deposit(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatDepositBinding::bind)
            holder.attachImageLoader(R.id.profile)
            holder.attachOnClickListener(R.id.profile)
            holder.attachOnClickListener(R.id.deposit_wechat)
            holder.attachOnClickListener(R.id.deposit_alipay)
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_deposit
        }
    }

    class Time(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatTimeBinding::bind)
            binding.time.text = time.nano.yyyy_mm_dd_hh_mm_ss()
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_time
        }
    }

    class System(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            TODO("Not yet implemented")
        }

        override fun getLayout(): Int {
            TODO("Not yet implemented")
        }
    }

    companion object {

        @JvmStatic
        fun create(data: DbMessage): MsgItem {
            val myself = data.fromUserId == AppPreferences.userId
            return when (data.type) {
                TYPE_TEXT -> (if (myself) Text(data) else Text2(data)).ofText()
                TYPE_IMAGE -> (if (myself) Image(data) else Image2(data)).ofImage()
                TYPE_AUDIO -> (if (myself) Audio(data) else Audio2(data)).ofAudio()
                TYPE_TIME -> Time(data).ofTime()
                TYPE_SYSTEM -> System(data).ofSystem()
                TYPE_DEPOSIT -> Deposit(data).ofDeposit()
                else -> Upgrade(data).ofUpgrade()
            }
        }
    }
}