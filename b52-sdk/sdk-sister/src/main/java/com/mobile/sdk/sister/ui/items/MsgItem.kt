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

    fun ofText(): MsgItem = this as Text

    fun ofImage(): MsgItem = this as Image

    fun ofAudio(): MsgItem = this as Audio

    fun ofText2(): MsgItem = this as Text2

    fun ofImage2(): MsgItem = this as Image2

    fun ofAudio2(): MsgItem = this as Audio2

    fun ofDeposit(): MsgItem = this as Deposit

    fun ofTime(): MsgItem = this as Time

    fun ofSystem(): MsgItem = this as System

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