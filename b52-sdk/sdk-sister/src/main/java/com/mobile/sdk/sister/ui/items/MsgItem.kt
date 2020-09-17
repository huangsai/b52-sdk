package com.mobile.sdk.sister.ui.items

import com.mobile.guava.jvm.date.yyyy_mm_dd_hh_mm_ss
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.http.*
import com.mobile.sdk.sister.databinding.*
import com.mobile.sdk.sister.ui.*
import com.mobile.sdk.sister.ui.views.MsgStatusImageView.Companion.STATUS_FAIL
import com.mobile.sdk.sister.ui.views.MsgStatusImageView.Companion.STATUS_SENDING
import com.mobile.sdk.sister.ui.views.MsgStatusImageView.Companion.STATUS_SUCCESS
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

abstract class MsgItem(val data: ApiMessage) : SimpleRecyclerItem() {

    lateinit var text: ApiMessage.Text
        private set

    lateinit var audio: ApiMessage.Audio
        private set

    lateinit var image: ApiMessage.Image
        private set

    lateinit var deposit: ApiMessage.Deposit
        private set

    lateinit var time: ApiMessage.Time
        private set

    lateinit var system: ApiMessage.System
        private set

    lateinit var upgrade: ApiMessage.Upgrade
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

    internal fun AdapterViewHolder.failClick(status: Int) {
        if (status == STATUS_FAIL) {
            attachOnClickListener(R.id.status)
        }
    }

    class Text(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatToTextBinding::bind)
            binding.textContent.text = text.msg
            binding.status.status = STATUS_SUCCESS
            holder.profileHandle()
            holder.failClick(binding.status.status)
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_to_text
        }
    }

    class Image(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatToImageBinding::bind)
            binding.status.status = STATUS_SENDING
            holder.profileHandle()
            holder.imageHandle()
            holder.failClick(binding.status.status)
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_to_image
        }
    }

    class Audio(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatToAudioBinding::bind)
            binding.audioContent.text = "${audio.duration / 1000}''"
            binding.status.status = STATUS_FAIL
            holder.profileHandle()
            holder.audioClick()
            holder.failClick(binding.status.status)
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_to_audio
        }
    }

    class Text2(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatFromTextBinding::bind)
            binding.textContent.text = text.msg
            holder.profileHandle()
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_from_text
        }
    }

    class Image2(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatFromImageBinding::bind)
            holder.profileHandle()
            holder.imageHandle()
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_from_image
        }
    }

    class Audio2(data: ApiMessage) : MsgItem(data) {

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

    class Upgrade(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            TODO("Not yet implemented")
        }

        override fun getLayout(): Int {
            TODO("Not yet implemented")
        }
    }

    class Upgrade2(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            TODO("Not yet implemented")
        }

        override fun getLayout(): Int {
            TODO("Not yet implemented")
        }
    }

    class Deposit(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatDepositBinding::bind)
            holder.profileHandle()
            holder.depositClick()
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_deposit
        }
    }

    class Time(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatTimeBinding::bind)
            binding.time.text = time.nano.yyyy_mm_dd_hh_mm_ss()
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_time
        }
    }

    class System(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            TODO("Not yet implemented")
        }

        override fun getLayout(): Int {
            TODO("Not yet implemented")
        }
    }

    companion object {

        @JvmStatic
        fun create(data: ApiMessage): MsgItem {
            return when (data.type) {
                TYPE_TEXT -> (if (data.toUserId == 0L) Text(data) else Text2(data)).ofText()
                TYPE_IMAGE -> (if (data.toUserId == 0L) Image(data) else Image2(data)).ofImage()
                TYPE_AUDIO -> (if (data.toUserId == 0L) Audio(data) else Audio2(data)).ofAudio()
                TYPE_TIME -> Time(data).ofTime()
                TYPE_SYSTEM -> System(data).ofSystem()
                TYPE_DEPOSIT -> Deposit(data).ofDeposit()
                else -> Upgrade(data).ofUpgrade()
            }
        }
    }
}