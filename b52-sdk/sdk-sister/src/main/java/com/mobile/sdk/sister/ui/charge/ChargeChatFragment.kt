package com.mobile.sdk.sister.ui.charge

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.commit
import com.mobile.guava.jvm.extension.cast
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.data.http.TYPE_DEPOSIT_ALIPAY
import com.mobile.sdk.sister.data.http.TYPE_DEPOSIT_UNION
import com.mobile.sdk.sister.data.http.TYPE_DEPOSIT_WECHAT
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.TopMainFragment
import com.mobile.sdk.sister.ui.chat.*
import com.mobile.sdk.sister.ui.views.MyKeyboardHelper

/**
 * 充值聊天页面
 */
class ChargeChatFragment : TopMainFragment(), View.OnClickListener, TextWatcher,
    View.OnKeyListener {

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
        fun newInstance(): ChargeChatFragment = ChargeChatFragment()
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
        chatListPresenter = ChatListPresenter(this, binding, fParent.model, true)
        chatHelpPresenter = ChatHelpPresenter(this, binding, fParent.model, true)
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
        binding.layoutTitle.visibility = View.VISIBLE
        binding.chatBack.setOnClickListener(this)
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
                    chatListPresenter.postChargeText(textContent)
                } else {
                    chatMorePresenter.showPop()
                }
            }
            R.id.chat_emotion -> chatEmotionPresenter.showPop()
            R.id.chat_back -> parentFragmentManager.popBackStack()
        }
    }

    override fun onBusEvent(event: Pair<Int, Any>) {
        if (event.first == SisterX.BUS_MSG_STATUS) {
            chatListPresenter.onMessageStatusChanged(event.second.cast())
            return
        }
        if (event.first == SisterX.BUS_MSG_NEW) {
            chatListPresenter.onNewMessage(event.second.cast())
            return
        }
        if (event.first == SisterX.BUS_MSG_AUTO_REPLY) {
            fParent.model.postSysReply(false, event.second.cast())
            return
        }
        //点击微信充值
        if (event.first == SisterX.BUS_CLICK_WECHAT) {
            jumpPaymentFragment(TYPE_DEPOSIT_WECHAT)
            return
        }
        //点击支付宝充值
        if (event.first == SisterX.BUS_CLICK_ALIPAY) {
            jumpPaymentFragment(TYPE_DEPOSIT_ALIPAY)
            return
        }
        //点击银行卡充值
        if (event.first == SisterX.BUS_CLICK_UNION) {
            jumpPaymentFragment(TYPE_DEPOSIT_UNION)
            return
        }
    }

    /**
     * 跳转充值页面
     */
    private fun jumpPaymentFragment(depositType: Int) {
        parentFragmentManager.commit {
            addToBackStack(null).hide(this@ChargeChatFragment).add(
                R.id.layout_fragment,
                ChargePaymentFragment.newInstance(depositType),
                ChargePaymentFragment.javaClass.simpleName
            )
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