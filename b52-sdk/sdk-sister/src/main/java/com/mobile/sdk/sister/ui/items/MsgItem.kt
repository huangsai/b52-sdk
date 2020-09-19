package com.mobile.sdk.sister.ui.items

import android.view.View
import androidx.core.view.isVisible
import com.mobile.guava.jvm.date.yyyy_mm_dd_hh_mm_ss
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.file.AppPreferences
import com.mobile.sdk.sister.data.http.*
import com.mobile.sdk.sister.databinding.*
import com.mobile.sdk.sister.ui.*
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

internal fun AdapterViewHolder.profileHandle() {
    attachImageLoader(R.id.profile)
    attachOnClickListener(R.id.profile)
}

internal fun AdapterViewHolder.imageHandle() {
    attachImageLoader(R.id.image_content)
    attachOnClickListener(R.id.image_content)
}

internal fun AdapterViewHolder.audioClick() {
    attachOnClickListener(R.id.audio_content)
}

internal fun AdapterViewHolder.depositClick() {
    attachOnClickListener(R.id.deposit_wechat)
    attachOnClickListener(R.id.deposit_alipay)
}

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

    internal fun ofText(): MsgItem {
        text = data.content.jsonToText()
        return this
    }

    internal fun ofImage(): MsgItem {
        image = data.content.jsonToImage()
        return this
    }

    internal fun ofAudio(): MsgItem {
        audio = data.content.jsonToAudio()
        return this
    }

    internal fun ofDeposit(): MsgItem {
        deposit = data.content.jsonToDeposit()
        return this
    }

    internal fun ofTime(): MsgItem {
        time = data.content.jsonToTime()
        return this
    }

    internal fun ofSystem(): MsgItem {
        system = data.content.jsonToSystem()
        return this
    }

    internal fun ofUpgrade(): MsgItem {
        upgrade = data.content.jsonToUpgrade()
        return this
    }

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

    class Text(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatToTextBinding::bind)
            binding.textContent.text = text.msg
            setStatus(binding.statusFailed, binding.statusProcessing)
            holder.attachOnClickListener(R.id.status_failed)
            holder.profileHandle()
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_to_text
        }
    }

    class Image(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatToImageBinding::bind)
            setStatus(binding.statusFailed, binding.statusProcessing)
            holder.attachOnClickListener(R.id.status_failed)

            holder.profileHandle()
            holder.imageHandle()
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_to_image
        }
    }

    class Audio(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatToAudioBinding::bind)
            binding.audioContent.text = "${audio.duration / 1000}''"
            setStatus(binding.statusFailed, binding.statusProcessing)
            holder.attachOnClickListener(R.id.status_failed)

            holder.profileHandle()
            holder.audioClick()
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_to_audio
        }
    }

    class Text2(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatFromTextBinding::bind)
            binding.textContent.text = text.msg
            holder.profileHandle()
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_from_text
        }
    }

    class Image2(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatFromImageBinding::bind)
            holder.profileHandle()
            holder.imageHandle()
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_from_image
        }
    }

    class Audio2(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatFromAudioBinding::bind)
            binding.audioContent.text = "${audio.duration / 1000}''"
            holder.profileHandle()
            holder.audioClick()
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
            holder.profileHandle()
            holder.depositClick()
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