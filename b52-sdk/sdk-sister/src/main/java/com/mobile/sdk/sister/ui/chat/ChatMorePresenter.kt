package com.mobile.sdk.sister.ui.chat

import android.view.View
import android.widget.ImageView
import com.mobile.guava.android.mvvm.Msg
import com.mobile.guava.android.mvvm.lifecycle.SimplePresenter
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.SisterViewModel
import com.pacific.adapter.AdapterViewHolder
import com.skydoves.balloon.createBalloon

class ChatMorePresenter(
    fragment: ChatFragment,
    binding: SisterFragmentChatBinding,
    model: SisterViewModel
) : BaseChatPresenter(fragment, binding, model) {

    fun createPop() {
        val popWidth = binding.layoutInput.width
        val balloon = createBalloon(fragment.requireContext()) {
            setLayout(R.layout.sister_popup_chat_more)
            cornerRadius = 0f
            arrowVisible = false
            width = popWidth
            setBackgroundColorResource(R.color.sister_color_black_transparent)
        }
        balloon.showAlignTop(binding.layoutInput, 0, 10)
        balloon.getContentView().findViewById<ImageView>(R.id.iv_picture).setOnClickListener(this)
        balloon.getContentView().findViewById<ImageView>(R.id.iv_camera).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_picture -> {
                Msg.toast("点击照片")
            }
            R.id.iv_camera -> {
                Msg.toast("点击拍摄")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun load() {
        TODO("Not yet implemented")
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        TODO("Not yet implemented")
    }
}