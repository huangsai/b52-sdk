package com.mobile.sdk.sister.ui

import com.mobile.guava.android.mvvm.BaseFragment

abstract class TopMainFragment : BaseFragment() {

    protected val fParent: MainDialogFragment by lazy { parentFragment as MainDialogFragment }

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