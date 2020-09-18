package com.mobile.sdk.sister.ui.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.mobile.guava.android.io.FileUtils
import com.mobile.guava.jvm.extension.cast
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.base.PickContent
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.TopMainFragment


class ChatFragment : TopMainFragment(), View.OnClickListener, TextWatcher {

    lateinit var startCameraLaunch: ActivityResultLauncher<Void>
    lateinit var startPictureLaunch: ActivityResultLauncher<Void>

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startCameraLaunch =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                if (it != null) {
                    chatListPresenter.postImage(FileUtils.createFile(it, requireContext()))
                }
            }
        startPictureLaunch = registerForActivityResult(PickContent(requireContext())) {
            if (it != null) {
                chatListPresenter.postImage(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentChatBinding.inflate(inflater, container, false)
        chatListPresenter = ChatListPresenter(this, binding, fParent.model)
        chatHelpPresenter = ChatHelpPresenter(this, binding, fParent.model)
        chatMorePresenter = ChatMorePresenter(this, binding, fParent.model)
        chatVoicePresenter = ChatVoicePresenter(this, binding, fParent.model)
        binding.chatAdd.setOnClickListener(this)
        binding.chatEt.addTextChangedListener(this)
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
            R.id.chat_add -> {
                if (binding.chatAdd.isSelected) {
                    chatListPresenter.postText(binding.chatEt.text.toString().trim())
                } else {
                    chatMorePresenter.createPop()
                }
            }
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
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        binding.chatAdd.isSelected = binding.chatEt.text.toString().trim().isNotEmpty()
    }
}