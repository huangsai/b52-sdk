package com.mobile.sdk.sister.ui.chat

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import com.mobile.guava.jvm.extension.cast
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.TopMainFragment
import com.mobile.sdk.sister.ui.views.MyKeyboardHelper

/**
 * 客服聊天页面
 */
class ChatFragment : TopMainFragment(), View.OnClickListener, TextWatcher, View.OnKeyListener {

    private var _binding: SisterFragmentChatBinding? = null
    private val binding: SisterFragmentChatBinding get() = _binding!!

    private val textContent: String
        get() = binding.chatEt.text.toString().trim()

    private lateinit var chatHelpPresenter: ChatHelpPresenter
    private lateinit var chatMorePresenter: ChatMorePresenter
    private lateinit var chatVoicePresenter: ChatVoicePresenter
    private lateinit var chatEmotionPresenter: ChatEmotionPresenter

    companion object {
        @JvmStatic
        fun newInstance(): ChatFragment = ChatFragment()
    }

    private val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        private var mWindowHeight = 0
        private var mSoftKeyboardHeight = 0
        private var mIsKeyboardVisible = false

        override fun onGlobalLayout() {
            val outRect = Rect()
            fParent.dialog!!.window!!.decorView.getWindowVisibleDisplayFrame(outRect)
            val height: Int = outRect.height()
            if (mWindowHeight == 0) {
                mWindowHeight = height
            } else {
                if (mWindowHeight != height) {
                    mSoftKeyboardHeight = mWindowHeight - height
                }
                if (mIsKeyboardVisible != MyKeyboardHelper.isKeyboardVisible(fParent.dialog!!)) {
                    mIsKeyboardVisible = !mIsKeyboardVisible
                    onKeyboardVisibility(mIsKeyboardVisible, mSoftKeyboardHeight)
                }
            }
        }
    }

    private fun onKeyboardVisibility(isKeyboardVisible: Boolean, mSoftKeyboardHeight: Int) {
        if (isKeyboardVisible) {
            binding.root.setPadding(0, 0, 0, mSoftKeyboardHeight)
        } else {
            binding.root.setPadding(0, 0, 0, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentChatBinding.inflate(inflater, container, false)
        chatListPresenter = ChatListPresenter(this, binding, fParent.model, false)
        chatHelpPresenter = ChatHelpPresenter(this, binding, fParent.model, false)
        chatMorePresenter = ChatMorePresenter(this, binding, fParent.model)
        chatVoicePresenter = ChatVoicePresenter(this, binding, fParent.model)
        chatEmotionPresenter = ChatEmotionPresenter(this, binding, fParent.model)
        binding.chatAdd.setOnClickListener(this)
        binding.chatEmotion.setOnClickListener(this)
        EmotionHandle.disableEmotion(binding.chatEt)
        binding.chatEt.addTextChangedListener(this)
        binding.chatEt.setOnKeyListener(this)
        fParent.dialog?.window?.decorView?.viewTreeObserver?.addOnGlobalLayoutListener(
            globalLayoutListener
        )
        return binding.root
    }

    override fun switchInputView(isVoice: Boolean) {
        binding.chatEt.visibility = if (isVoice) View.INVISIBLE else View.VISIBLE
        binding.pressVoice.visibility = if (isVoice) View.VISIBLE else View.INVISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SisterX.isLogin.observe(viewLifecycleOwner, { isLogin ->
            if (isLogin) {
                chatListPresenter.load()
                chatHelpPresenter.load()
            } else {
                chatListPresenter.cleanMessages()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fParent.dialog?.window?.decorView?.viewTreeObserver?.removeOnGlobalLayoutListener(
            globalLayoutListener
        )
        binding.chatEt.setOnKeyListener(null)
        binding.chatEt.removeTextChangedListener(this)
        chatHelpPresenter.onDestroyView()
        chatListPresenter.onDestroyView()
        chatEmotionPresenter.onDestroyView()
        _binding = null

        SisterX.isUiPrepared.value = false
    }

    override fun onDestroy() {
        super.onDestroy()
        chatListPresenter.onDestroy()
        chatHelpPresenter.onDestroy()
        chatMorePresenter.onDestroy()
        chatVoicePresenter.onDestroy()
        chatEmotionPresenter.onDestroy()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.chat_add -> {
                if (binding.chatAdd.isSelected) {
                    chatListPresenter.postText(textContent)
                } else {
                    chatMorePresenter.showPop()
                }
            }
            R.id.chat_emotion -> chatEmotionPresenter.showPop()
        }
    }

    override fun onBusEvent(event: Pair<Int, Any>) {
        //消息状态改变
        if (event.first == SisterX.BUS_MSG_STATUS) {
            chatListPresenter.onMessageStatusChanged(event.second.cast())
            return
        }
        //收到新消息
        if (event.first == SisterX.BUS_MSG_NEW) {
            chatListPresenter.onNewMessage(event.second.cast())
            return
        }
        //点击系统回复Item
        if (event.first == SisterX.BUS_MSG_AUTO_REPLY) {
            fParent.model.postSysReply(false, event.second.cast())
            return
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        binding.chatAdd.isSelected = textContent.isNotEmpty()
        chatEmotionPresenter.updateButtonStatus()
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP) {
            EmotionHandle.deleteEmotionText(binding.chatEt)
            return false
        }
        return true
    }
}