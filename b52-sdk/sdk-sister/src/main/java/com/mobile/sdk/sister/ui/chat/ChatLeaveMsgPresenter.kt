package com.mobile.sdk.sister.ui.chat

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.mobile.guava.android.context.dp2px2
import com.mobile.guava.android.ime.ImeUtils
import com.mobile.guava.android.mvvm.BaseFragment
import com.mobile.guava.android.mvvm.Msg
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.SisterViewModel
import com.mobile.sdk.sister.ui.TopMainFragment
import com.pacific.adapter.AdapterViewHolder
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.createBalloon
import com.skydoves.balloon.overlay.BalloonOverlayAnimation

class ChatLeaveMsgPresenter(
    fragment: TopMainFragment,
    binding: SisterFragmentChatBinding,
    model: SisterViewModel,
) : BaseChatPresenter(fragment, binding, model) {

    private val maxInputDesCount = 200
    private val inputContentEt: EditText
    private val inputNumTv: TextView

    private var balloon: Balloon = createBalloon(fragment.requireContext()) {
        setLayout(R.layout.sister_popup_chat_leave_msg)
        arrowVisible = false
        cornerRadius = 0f
        marginTop = (binding.chatRecycler.measuredHeight - fragment.requireContext().dp2px2(160f)) / 2
        width = binding.chatRecycler.width - 16
        setBackgroundDrawableResource(R.drawable.sister_leave_msg_bg)
        setElevation(0)
        setBalloonOverlayAnimation(BalloonOverlayAnimation.FADE)
        setLifecycleOwner(fragment)
    }

    init {
        balloon.getContentView().findViewById<ImageView>(R.id.btn_cancel).setOnClickListener(this)
        balloon.getContentView().findViewById<ImageView>(R.id.btn_submit).setOnClickListener(this)
        inputContentEt = balloon.getContentView().findViewById(R.id.input_content)
        inputNumTv = balloon.getContentView().findViewById(R.id.input_num)
        inputContentEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable) {
                inputNumTv.text = s.length.toString() + "/" + maxInputDesCount
            }
        })
    }

    fun showPop() {
        balloon.showAlignTop(binding.chatRecycler)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_submit -> {
                ImeUtils.hideIme(inputContentEt)
                val content = inputContentEt.text.toString().trim()
                if (content.isNullOrEmpty() || content.length < 10) {
                    Msg.toast(R.string.sister_leave_msg_input_content_toast)
                    return
                }
                model.leaveMessage(content)
            }
        }
        balloon.dismiss()
    }


    override fun load() {
        TODO("Not yet implemented")
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
    }
}