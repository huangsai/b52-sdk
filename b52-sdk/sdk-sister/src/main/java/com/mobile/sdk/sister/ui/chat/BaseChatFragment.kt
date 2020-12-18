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
import com.mobile.sdk.sister.ui.SisterDialogFragment
import com.mobile.sdk.sister.ui.views.MyKeyboardHelper

/**
 * 客服聊天页面
 */
abstract class BaseChatFragment : SisterDialogFragment.MyFragment(), View.OnClickListener, TextWatcher, View.OnKeyListener {

    private var _binding: SisterFragmentChatBinding? = null
    protected val binding: SisterFragmentChatBinding get() = _binding!!

    protected val textContent: String
        get() = binding.chatEt.text.toString().trim()

    protected lateinit var chatHelpPresenter: ChatHelpPresenter
    protected lateinit var chatMorePresenter: ChatMorePresenter
    protected lateinit var chatVoicePresenter: ChatVoicePresenter
    protected lateinit var chatEmotionPresenter: ChatEmotionPresenter

    private val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        private var mWindowHeight = 0
        private var mSoftKeyboardHeight = 0
        private var mIsKeyboardVisible = false

        override fun onGlobalLayout() {
            val outRect = Rect()
            pFragment.dialog!!.window!!.decorView.getWindowVisibleDisplayFrame(outRect)
            val height: Int = outRect.height()
            if (mWindowHeight == 0) {
                mWindowHeight = height
            } else {
                if (mWindowHeight != height) {
                    mSoftKeyboardHeight = mWindowHeight - height
                }
                if (mIsKeyboardVisible != MyKeyboardHelper.isKeyboardVisible(pFragment.dialog!!)) {
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
        chatListPresenter = ChatListPresenter(this, binding, viewModel, false)
        chatHelpPresenter = ChatHelpPresenter(this, binding, viewModel, false)
        chatMorePresenter = ChatMorePresenter(this, binding, viewModel)
        chatVoicePresenter = ChatVoicePresenter(this, binding, viewModel)
        chatEmotionPresenter = ChatEmotionPresenter(this, binding, viewModel)
        binding.chatAdd.setOnClickListener(this)
        binding.chatEmotion.setOnClickListener(this)
        EmotionHandle.disableEmotion(binding.chatEt)
        binding.chatEt.addTextChangedListener(this)
        binding.chatEt.setOnKeyListener(this)
        pFragment.dialog?.window?.decorView?.viewTreeObserver?.addOnGlobalLayoutListener(
            globalLayoutListener
        )
        return binding.root
    }

    override fun setInputView(isVoiceEnable: Boolean) {
        binding.chatEt.visibility = if (isVoiceEnable) View.INVISIBLE else View.VISIBLE
        binding.pressVoice.visibility = if (isVoiceEnable) View.VISIBLE else View.INVISIBLE
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
        pFragment.dialog?.window?.decorView?.viewTreeObserver?.removeOnGlobalLayoutListener(
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
            viewModel.postSysReply(false, event.second.cast())
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

    override fun load() {
    }
}