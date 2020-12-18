package com.mobile.sdk.sister.ui.items

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.AnimationDrawable
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.guava.android.ui.view.text.MySpannable
import com.mobile.guava.jvm.coroutines.Bus
import com.mobile.guava.jvm.date.yyyy_mm_dd_hh_mm
import com.mobile.guava.jvm.extension.cast
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.http.*
import com.mobile.sdk.sister.databinding.*
import com.mobile.sdk.sister.ui.*
import com.mobile.sdk.sister.ui.chat.EmotionHandle
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import com.pacific.adapter.SimpleRecyclerItem
import timber.log.Timber

/**
 * 聊天消息条目
 */
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

    /**
     * 发出文本消息Item
     */
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

    /**
     * 发出图片消息Item
     */
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

    /**
     * 发出语音消息Item
     */
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

    /**
     * 收到文本消息Item
     */
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

    /**
     * 收到图片消息Item
     */
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

    /**
     * 收到语音消息Item
     */
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

    /**
     * 发出不支持消息类型Item
     */
    class Upgrade(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatToTextBinding::bind)
            binding.textContent.text = "不支持该类型消息，请升级"
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_to_text
        }
    }

    /**
     * 收到不支持消息类型Item
     */
    class Upgrade2(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatFromTextBinding::bind)
            binding.textContent.text = "不支持该类型消息，请升级"
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_from_text
        }
    }

    /**
     * 留言消息Item
     */
    class LeaveMsg(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatLeaveMsgBinding::bind)
            binding.leaveMsgClick.paint.flags = Paint.UNDERLINE_TEXT_FLAG
            binding.leaveMsgClick.paint.isAntiAlias = true

            binding.textContent.text = text.msg
            holder.attachImageLoader(R.id.profile)
            holder.attachOnClickListener(R.id.profile)
            holder.attachOnClickListener(R.id.leave_msg_click)
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_leave_msg
        }
    }

    /**
     * 联系客服消息Item
     */
    class Robot(data: DbMessage) : MsgItem(data), View.OnClickListener {
        private val adapter: RecyclerAdapter by lazy {
            RecyclerAdapter().apply {
                addAll(robot.map { AutoReplyItem(it) })
            }
        }

        private val mySpannable by lazy {
            MySpannable().apply {
                append(
                    if (robot.isNullOrEmpty()) {
                        "对不起，没有查询到您发送的问题，您可试试\n联系客服"
                    } else {
                        "如果以上答案未解决您的问题，请点击\n联系客服"
                    }
                )
                findAndSpan("联系客服") { UnderlineSpan() }
                findAndSpan("联系客服") {
                    ForegroundColorSpan(Color.parseColor("#2271e6"))
                }
            }
        }

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatAutoReplyBinding::bind)

            binding.autoReplyClick.text = mySpannable
            holder.attachImageLoader(R.id.profile)
            holder.attachOnClickListener(R.id.profile)
            holder.attachOnClickListener(R.id.auto_reply_click)

            if (robot.isNullOrEmpty()) {
                binding.autoReplyRecycler.visibility = View.GONE
                binding.textTitle.visibility = View.GONE
            } else {
                binding.autoReplyRecycler.visibility = View.VISIBLE
                binding.textTitle.visibility = View.VISIBLE
                adapter.onClickListener = this
                binding.autoReplyRecycler.layoutManager = LinearLayoutManager(
                    holder.itemView.context
                )
                binding.autoReplyRecycler.adapter = adapter
            }
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_auto_reply
        }

        override fun onClick(v: View) {
            val data = AdapterUtils.getHolder(v).item<AutoReplyItem>().data
            Bus.offer(SisterX.BUS_MSG_AUTO_REPLY, data)
        }
    }

    /**
     * 充值消息Item
     */
    class Deposit(data: DbMessage) : MsgItem(data) {

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatDepositBinding::bind)
            holder.attachImageLoader(R.id.profile)
            holder.attachOnClickListener(R.id.profile)
            holder.attachOnClickListener(R.id.deposit_wechat)
            holder.attachOnClickListener(R.id.deposit_alipay)
            holder.attachOnClickListener(R.id.deposit_union)
            //根据充值类型显示不同充值条目:微信-支付宝-银行卡
            when (deposit.type) {
                TYPE_DEPOSIT_ALIPAY -> {
                    binding.depositAlipay.visibility = View.VISIBLE
                }
                TYPE_DEPOSIT_WECHAT -> {
                    binding.depositWechat.visibility = View.VISIBLE
                }
                TYPE_DEPOSIT_UNION -> {
                    binding.depositUnion.visibility = View.VISIBLE
                }
                else -> throw IllegalStateException()
            }
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_deposit
        }
    }

    /**
     * 时间Item
     */
    class Time(data: DbMessage) : MsgItem(data) {
        private val nanoStr: String by lazy {
            time.nano.yyyy_mm_dd_hh_mm()
        }

        override fun bind(holder: AdapterViewHolder) {
            val binding = holder.binding(SisterItemChatTimeBinding::bind)
            binding.time.text = nanoStr
        }

        override fun getLayout(): Int {
            return R.layout.sister_item_chat_time
        }
    }

    /**
     * 系统通知Item
     */
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
                    TYPE_TEXT -> {
                        (if (data.isFromSister()) Text2(data).ofText() else Text(data)).ofText()
                    }
                    TYPE_IMAGE -> {
                        (if (data.isFromSister()) Image2(data).ofImage() else Image(data)).ofImage()
                    }
                    TYPE_AUDIO -> {
                        (if (data.isFromSister()) Audio2(data).ofAudio() else Audio(data)).ofAudio()
                    }
                    TYPE_TIME -> Time(data).ofTime()
                    TYPE_SYSTEM -> System(data).ofSystem()
                    TYPE_DEPOSIT -> Deposit(data).ofDeposit()
                    TYPE_LEAVE_MSG -> LeaveMsg(data).ofText()
                    TYPE_ROBOT -> Robot(data).ofRobot()
                    else -> Upgrade(data).ofUpgrade()
                }
            } catch (e: Exception) {
                Timber.d(e)
                Upgrade(data).ofUpgrade()
            }
        }
    }
}