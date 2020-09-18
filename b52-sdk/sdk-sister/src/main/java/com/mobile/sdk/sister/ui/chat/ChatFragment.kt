package com.mobile.sdk.sister.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.guava.jvm.extension.cast
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.TopMainFragment

class ChatFragment : TopMainFragment(), View.OnClickListener {

    private var _binding: SisterFragmentChatBinding? = null
    private val binding: SisterFragmentChatBinding get() = _binding!!

    private lateinit var chatListPresenter: ChatListPresenter
    private lateinit var chatHelpPresenter: ChatHelpPresenter
    private lateinit var chatMorePresenter: ChatMorePresenter
    private lateinit var chatVoicePresenter: ChatVoicePresenter

    companion object {
        @JvmStatic
        fun newInstance(): ChatFragment = ChatFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentChatBinding.inflate(inflater, container, false)
        chatListPresenter = ChatListPresenter(this, binding, fParent.model)
        chatHelpPresenter = ChatHelpPresenter(this, binding, fParent.model)
        chatMorePresenter = ChatMorePresenter(this, binding)
        chatVoicePresenter = ChatVoicePresenter(this, binding)
        binding.chatAdd.setOnClickListener(this)
        return binding.root
    }

    override fun switchInputView(isVoice: Boolean) {
        binding.chatEt.visibility = if (isVoice) View.INVISIBLE else View.VISIBLE
        binding.pressVoice.visibility = if (isVoice) View.VISIBLE else View.INVISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatListPresenter.load()
        chatHelpPresenter.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        chatHelpPresenter.onDestroyView()
        chatListPresenter.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.chat_add -> chatMorePresenter.createPop()
        }
    }

    override fun onBusEvent(event: Pair<Int, Any>) {
        if (event.first == SisterX.BUS_MSG_STATUS) {
            chatListPresenter.onMessageStatusChanged(event.second.cast())
            return
        }
    }
}