package com.mobile.sdk.sister.ui.chat

import android.os.Bundle

class ChatFragment1 : BaseChatFragment() {

    override fun onResume() {
        super.onResume()
        pFragment.setFlag(1)
    }

    companion object {

        @JvmStatic
        fun newInstance(): ChatFragment1 = ChatFragment1().apply {
            arguments = Bundle().apply {
            }
        }
    }
}