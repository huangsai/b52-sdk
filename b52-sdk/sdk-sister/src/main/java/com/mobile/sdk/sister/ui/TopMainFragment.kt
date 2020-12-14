package com.mobile.sdk.sister.ui

import com.mobile.guava.android.mvvm.BaseFragment
import com.mobile.sdk.sister.ui.chat.ChatListPresenter

abstract class TopMainFragment : BaseFragment() {

    protected val fParent: MainDialogFragment by lazy {
        if (parentFragment is MainDialogFragment) {
            parentFragment as MainDialogFragment
        } else {
            parentFragment?.parentFragment as MainDialogFragment
        }
    }

    lateinit var chatListPresenter: ChatListPresenter

    override fun onResume() {
        super.onResume()
        fParent.currentFragment = this
    }

    override fun onDestroy() {
        super.onDestroy()
        if (fParent.currentFragment == this) {
            fParent.currentFragment = null
        }
    }

    open fun switchInputView(isVoice: Boolean) {
    }
}