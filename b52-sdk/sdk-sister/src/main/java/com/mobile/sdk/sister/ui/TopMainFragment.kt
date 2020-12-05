package com.mobile.sdk.sister.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
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

    /**
     * Fragment的切换 只隐藏
     */
    open fun switchFragment(
        currentFragment: Fragment,
        targetFragment: Fragment,
        containerView: Int
    ): FragmentTransaction? {
        // 获取事物的实例
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        // 判断此fragment是否添加
        if (!targetFragment.isAdded) { // 没有添加
            //注意 :  第一次使用switchFragment()时currentFragment为null，所以要判断一下
            transaction.hide(currentFragment)
            // 添加对应的fragment
            transaction.add(containerView, targetFragment, targetFragment.javaClass.simpleName)
        } else {
            transaction
                .hide(currentFragment)
                .show(targetFragment)
        }
        return transaction
    }

}