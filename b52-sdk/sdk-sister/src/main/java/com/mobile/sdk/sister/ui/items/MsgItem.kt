package com.mobile.sdk.sister.ui.items

import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.http.*
import com.mobile.sdk.sister.databinding.*
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

/**
 * 以2结尾的是客服发送的消息
 */
abstract class MsgItem(val data: ApiMessage) : SimpleRecyclerItem() {

    class Text(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatToTextBinding::bind)
            binding.content.text = data.content
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_to_text
        }
    }

    class Image(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatToImageBinding::bind)

        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_to_image
        }
    }

    class Audio(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatToAudioBinding::bind)
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_to_audio
        }
    }

    class Text2(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatFromTextBinding::bind)
            binding.content.text = data.content
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_from_text
        }
    }

    class Image2(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatFromImageBinding::bind)

        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_from_image
        }
    }

    class Audio2(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatFromAudioBinding::bind)
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_from_audio
        }
    }

    /**
     * 新版支持的消息类型，用于提示当前app版本过低，无法显示当前消息
     */
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
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_deposit
        }
    }

    class Time(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatTimeBinding::bind)
            binding.time.text = data.content
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_time
        }
    }

    class System(data: ApiMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemSystemNoticeBinding::bind)
            binding.systemContent.text = data.content
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_system_notice
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun ofText(): Text = this as Text

    @Suppress("UNCHECKED_CAST")
    fun ofImage(): Image = this as Image

    @Suppress("UNCHECKED_CAST")
    fun ofAudio(): Audio = this as Audio

    @Suppress("UNCHECKED_CAST")
    fun ofText2(): Text2 = this as Text2

    @Suppress("UNCHECKED_CAST")
    fun ofImage2(): Image2 = this as Image2

    @Suppress("UNCHECKED_CAST")
    fun ofAudio2(): Audio2 = this as Audio2

    @Suppress("UNCHECKED_CAST")
    fun ofDeposit(): Deposit = this as Deposit

    @Suppress("UNCHECKED_CAST")
    fun ofTime(): Time = this as Time

    @Suppress("UNCHECKED_CAST")
    fun ofSystem(): System = this as System

    companion object {

        @JvmStatic
        fun create(data: ApiMessage): MsgItem {
            return when (data.type) {
                TYPE_TEXT -> {
                    if (data.toUserId == 0L) Text(data) else Text2(data)
                }
                TYPE_IMAGE -> {
                    if (data.toUserId == 0L) Image(data) else Image2(data)
                }
                TYPE_AUDIO -> {
                    if (data.toUserId == 0L) Audio(data) else Audio2(data)
                }
                TYPE_TIME -> Time(data)
                TYPE_SYSTEM -> System(data)
                TYPE_DEPOSIT -> Deposit(data)
                else -> Upgrade(data)
            }
        }
    }
}