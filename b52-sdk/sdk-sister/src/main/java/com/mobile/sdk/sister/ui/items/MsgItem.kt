package com.mobile.sdk.sister.ui.items

import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.SisterLib
import com.mobile.sdk.sister.data.http.*
import com.mobile.sdk.sister.databinding.*
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem
import com.squareup.moshi.Types
import java.lang.reflect.Type

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
        text = SisterLib.component.json()
            .adapter<ApiMessage.Text>(subType(ApiMessage.Text::class.java))
            .fromJson(data.content)!!
        return this
    }

    internal fun ofImage(): MsgItem {
        image = SisterLib.component.json()
            .adapter<ApiMessage.Image>(subType(ApiMessage.Image::class.java))
            .fromJson(data.content)!!
        return this
    }

    internal fun ofAudio(): MsgItem {
        audio = SisterLib.component.json()
            .adapter<ApiMessage.Audio>(subType(ApiMessage.Audio::class.java))
            .fromJson(data.content)!!
        return this
    }

    internal fun ofDeposit(): MsgItem {
        deposit = SisterLib.component.json()
            .adapter<ApiMessage.Deposit>(subType(ApiMessage.Deposit::class.java))
            .fromJson(data.content)!!
        return this
    }

    internal fun ofTime(): MsgItem {
        time = SisterLib.component.json()
            .adapter<ApiMessage.Time>(subType(ApiMessage.Time::class.java))
            .fromJson(data.content)!!
        return this
    }

    internal fun ofSystem(): MsgItem {
        system = SisterLib.component.json()
            .adapter<ApiMessage.System>(subType(ApiMessage.System::class.java))
            .fromJson(data.content)!!
        return this
    }

    internal fun ofUpgrade(): MsgItem {
        upgrade = SisterLib.component.json()
            .adapter<ApiMessage.Upgrade>(subType(ApiMessage.Upgrade::class.java))
            .fromJson(data.content)!!
        return this
    }

    private fun subType(type: Type): Type {
        return Types.newParameterizedType(ApiMessage::class.java, type)
    }

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