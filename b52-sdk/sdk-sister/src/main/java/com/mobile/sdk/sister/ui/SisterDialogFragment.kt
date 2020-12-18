package com.mobile.sdk.sister.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mobile.guava.android.mvvm.BaseAppCompatDialogFragment
import com.mobile.guava.android.mvvm.BaseFragment
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.databinding.SisterFragmentDialogBinding
import com.mobile.sdk.sister.ui.chat.ChatFragment1
import com.mobile.sdk.sister.ui.chat.ChatListPresenter
import com.mobile.sdk.sister.ui.chat.SessionFragment

class SisterDialogFragment : BaseAppCompatDialogFragment(), View.OnClickListener {

    private var _binding: SisterFragmentDialogBinding? = null
    private val binding: SisterFragmentDialogBinding get() = _binding!!
    private var sCancelable = false
    private var flag = 0

    var tabIndex = 0
        private set

    var currentFragment: MyFragment? = null
        private set

    private val viewModel: SisterViewModel by viewModels {
        SisterX.component.viewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.sister_DialogFragment)
        arguments?.let {
            sCancelable = it.getBoolean("sCancelable")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentDialogBinding.inflate(inflater, container, false)
        isCancelable = sCancelable
        binding.viewPager.isUserInputEnabled = false
        binding.imgClose.setOnClickListener(this)
        binding.voiceBtn.setOnClickListener(this)
        binding.callBtn.setOnClickListener(this)
        binding.rbChat.setOnClickListener(this)
        binding.rbCharge.setOnClickListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter = MyAdapter(this)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { _window ->
            _window.setGravity(Gravity.LEFT)
            _window.setWindowAnimations(R.style.sister_DialogWindow)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.img_close -> {
                dismissAllowingStateLoss()
            }
            R.id.voice_btn -> {
                binding.voiceBtn.isSelected = !binding.voiceBtn.isSelected
                currentFragment?.setInputView(binding.voiceBtn.isSelected)
            }
            R.id.call_btn -> {
                error("暂未开放此功能")
            }
            R.id.rb_chat -> {
                tabIndex = 0
                onTabChanged()
            }
            R.id.rb_charge -> {
                tabIndex = 1
                onTabChanged()
            }
        }
    }

    /**
     * 切换左边栏顶部按钮
     */
    private fun onTabChanged() {
        binding.viewPager.setCurrentItem(tabIndex, false)
    }

    private fun loadSession() {
    }

    fun postText() {
    }

    fun postImage() {
    }

    fun postAudio() {
    }

    fun requestSister() {
    }

    /**
     * 显示或隐藏左边栏底部按钮
     */
    fun setFlag(newFlag: Int) {
        if (newFlag == flag) {
            return
        }
        flag = newFlag
        when (flag) {
            1 -> {
                binding.voiceBtn.isInvisible = false
            }
            2 -> {
                binding.voiceBtn.isInvisible = false
            }
            3 -> {
                binding.voiceBtn.isInvisible = true
            }
            else -> error("Sorry")
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(sCancelable: Boolean): SisterDialogFragment {
            return SisterDialogFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("sCancelable", sCancelable)
                }
            }
        }
    }

    class MyAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun createFragment(position: Int): Fragment {
            if (0 == position) {
                return ChatFragment1.newInstance()
            }
            return SessionFragment.newInstance()
        }

        override fun getItemCount(): Int {
            return 2
        }
    }

    abstract class MyFragment : BaseFragment() {

        protected val pFragment: SisterDialogFragment by lazy {
            findSisterDialogFragment(this)
        }
        protected val viewModel: SisterViewModel get() = pFragment.viewModel

        lateinit var chatListPresenter: ChatListPresenter

        private fun findSisterDialogFragment(fragment: Fragment): SisterDialogFragment {
            return if (fragment.requireParentFragment() is SisterDialogFragment) {
                fragment.requireParentFragment() as SisterDialogFragment
            } else {
                findSisterDialogFragment(fragment.requireParentFragment())
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            load()
        }

        override fun onResume() {
            super.onResume()
            pFragment.currentFragment = this
        }

        override fun onDestroy() {
            super.onDestroy()
            if (pFragment.currentFragment == this) {
                pFragment.currentFragment = null
            }
        }

        open fun setInputView(isVoiceEnable: Boolean) {
        }

        protected abstract fun load()
    }
}