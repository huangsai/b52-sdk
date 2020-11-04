package com.mobile.sdk.sister.ui.items

import android.graphics.Paint
import android.graphics.drawable.AnimationDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.jvm.date.yyyy_mm_dd_hh_mm_ss
import com.mobile.guava.jvm.extension.cast
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.http.*
import com.mobile.sdk.sister.databinding.*
import com.mobile.sdk.sister.ui.*
import com.mobile.sdk.sister.ui.chat.EmotionHandle
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
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

    lateinit var robot: List<ApiSysReply>
        private set

    var isAudioPlaying: Boolean = false

    internal fun ofText(): MsgItem = apply { text = data.content.jsonToText() }

    internal fun ofImage(): MsgItem = apply { image = data.content.jsonToImage() }

    internal fun ofAudio(): MsgItem = apply { audio = data.content.jsonToAudio() }

    internal fun ofDeposit(): MsgItem = apply { deposit = data.content.jsonToDeposit() }

    internal fun ofTime(): MsgItem = apply { time = data.content.jsonToTime() }

    internal fun ofSystem(): MsgItem = apply { system = data.content.jsonToSystem() }

    internal fun ofUpgrade(): MsgItem = apply { upgrade = data.content.jsonToUpgrade() }

    internal fun ofRobot(): MsgItem = apply { robot = data.content.jsonToRobot() }

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
            view.setImageResource(R.drawable.sister_audio_play_to_anim)
            view.drawable.cast<AnimationDrawable>().start()
        } else {
            if (view.drawable is AnimationDrawable) {
                view.drawable.cast<AnimationDrawable>().stop()
            }
            view.setImageResource(R.drawable.sister_icon_chat_to_audio)
        }
    }

    protected fun applyIsAudio2Play(view: ImageView) {
        if (isAudioPlaying) {
            view.setImageResource(R.drawable.sister_audio_play_from_anim)
            view.drawable.cast<AnimationDrawable>().start()
        } else {
            if (view.drawable is AnimationDrawable) {
                view.drawable.cast<AnimationDrawable>().stop()
            }
            view.setImageResource(R.drawable.sister_icon_chat_from_audio)

        }
    }

    class Text(data: DbMessage) : MsgItem(data) {

        private lateinit var binding: SisterItemChatToTextBinding
        private val spannable by lazy {
            EmotionHandle.buildEmotionSpannable(
                text.msg,
                binding.textContent.textSize.toInt()
            )
        }

        override fun bind(holder: AdapterViewHolder) {
            binding = holder.binding(SisterItemChatToTextBinding::bind)
            binding.textContent.text = spannable
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
                return
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
            if (binding.audioImg.drawable is AnimationDrawable)
                binding.audioImg.drawable.cast<AnimationDrawable>().stop()
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_to_audio
        }
    }

    class Text2(data: DbMessage) : MsgItem(data) {

        private lateinit var binding: SisterItemChatFromTextBinding
        private val spannable by lazy {
            EmotionHandle.buildEmotionSpannable(
                text.msg,
                binding.textContent.textSize.toInt()
            )
        }

        override fun bind(holder: AdapterViewHolder) {
            binding = holder.binding(SisterItemChatFromTextBinding::bind)
            binding.textContent.text = spannable
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
            applyIsAudio2Play(binding.audioImg)
            holder.attachImageLoader(R.id.profile)
            holder.attachOnClickListener(R.id.profile)
            holder.attachOnClickListener(R.id.layout_audio)
        }

        override fun bindPayloads(holder: AdapterViewHolder, payloads: List<Any>?) {
            if (payloads.isNullOrEmpty()) return
            val event = payloads[0] as Int

            val binding: SisterItemChatFromAudioBinding = holder.binding()
            if (event == SisterX.BUS_MSG_AUDIO_PLAYING) {
                applyIsAudio2Play(binding.audioImg)
                return
            }
        }

        override fun unbind(holder: AdapterViewHolder) {
            val binding: SisterItemChatFromAudioBinding = holder.binding()
            if (binding.audioImg.drawable is AnimationDrawable)
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

    class LeaveMsg(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatLeaveMsgBinding::bind)
            binding.leaveMsgClick.paint.flags = Paint.UNDERLINE_TEXT_FLAG
            binding.leaveMsgClick.paint.isAntiAlias = true
            holder.attachImageLoader(R.id.profile)
            holder.attachOnClickListener(R.id.profile)
            holder.attachOnClickListener(R.id.leave_msg_click)
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_leave_msg
        }
    }

    class Robot(data: DbMessage) : MsgItem(data) {
        private val content = "如果以上答案未解决您的问题，请点击 联系客服"
        private val underlineSpan = UnderlineSpan()
        private val adapter = RecyclerAdapter()

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatAutoReplyBinding::bind)
            val spannableString = SpannableStringBuilder()
            spannableString.append(content)
            spannableString.setSpan(
                underlineSpan,
                content.length - 4,
                content.length,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            binding.autoReplyClick.text = spannableString
            holder.attachImageLoader(R.id.profile)
            holder.attachOnClickListener(R.id.profile)
            holder.attachOnClickListener(R.id.auto_reply_click)
            // 使用robot变量绑定数据
            binding.autoReplyRecycler.layoutManager = LinearLayoutManager(AndroidX.myApp)
            binding.autoReplyRecycler.adapter = adapter
            adapter.replaceAll(robot.map { AutoReplyItem(it) })
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_auto_reply
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
        private val nanoStr: String by lazy {
            time.nano.yyyy_mm_dd_hh_mm_ss()
        }

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatTimeBinding::bind)
            binding.time.text = nanoStr
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_time
        }
    }

    class System(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_time
        }
    }

    companion object {

        @JvmStatic
        fun create(data: DbMessage): MsgItem {
            return try {
                when (data.type) {
                    TYPE_TEXT, TYPE_TEXT_A, TYPE_TEXT_B -> {
                        (if (data.isSister()) Text2(data).ofText() else Text(data)).ofText()
                    }
                    TYPE_IMAGE -> {
                        (if (data.isSister()) Image2(data).ofImage() else Image(data)).ofImage()
                    }
                    TYPE_AUDIO -> {
                        (if (data.isSister()) Audio2(data).ofAudio() else Audio(data)).ofAudio()
                    }
                    TYPE_TIME -> Time(data).ofTime()
                    TYPE_SYSTEM -> System(data).ofSystem()
                    TYPE_DEPOSIT -> Deposit(data).ofDeposit()
                    TYPE_LEAVE_MSG -> LeaveMsg(data)
                    TYPE_ROBOT -> Robot(data).ofRobot()
                    else -> Upgrade(data).ofUpgrade()
                }
            } catch (e: Exception) {
                Upgrade(data).ofUpgrade()
            }
        }
    }
}