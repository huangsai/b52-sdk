package com.mobile.sdk.sister.ui.chat

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager

class ChatFragment2 : BaseChatFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.layoutTitle.isVisible = true
        binding.layoutTitle.setOnClickListener {
            requireParentFragment().childFragmentManager.popBackStack(
                this.javaClass.simpleName,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
    }

    override fun onResume() {
        super.onResume()
        pFragment.setFlag(2)
    }

    companion object {

        @JvmStatic
        fun newInstance(): ChatFragment2 = ChatFragment2().apply {
            arguments = Bundle().apply {
            }
        }
    }
}