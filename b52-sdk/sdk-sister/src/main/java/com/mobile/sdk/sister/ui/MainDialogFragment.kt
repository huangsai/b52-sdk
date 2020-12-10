package com.mobile.sdk.sister.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.android.mvvm.BaseAppCompatDialogFragment
import com.mobile.guava.android.mvvm.Msg
import com.mobile.guava.android.ui.screen.screen
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.bubble.Bubble
import com.mobile.sdk.sister.bubble.permission.OnPermissionResult
import com.mobile.sdk.sister.bubble.permission.PermissionUtils
import com.mobile.sdk.sister.data.http.BUZ_LOGOUT_MSG
import com.mobile.sdk.sister.databinding.SisterDialogMainBinding
import com.mobile.sdk.sister.ui.charge.ChargeChatFragment
import com.mobile.sdk.sister.ui.charge.ChargeFragment
import com.mobile.sdk.sister.ui.chat.ChatFragment
import kotlin.math.max
import kotlin.math.min


class MainDialogFragment : BaseAppCompatDialogFragment(), View.OnClickListener {

    private var _binding: SisterDialogMainBinding? = null
    private val binding: SisterDialogMainBinding get() = _binding!!

    private var sCancelable: Boolean = false
    private var tabPosition = 0

    val model: SisterViewModel by viewModels { SisterX.component.viewModelFactory() }
    var currentFragment: TopMainFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyDialogCount = true
        arguments?.let {
            sCancelable = it.getBoolean("cancelable")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(sCancelable)
        dialog?.setCanceledOnTouchOutside(sCancelable)
        _binding = SisterDialogMainBinding.inflate(inflater, container, false)
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.adapter = MyAdapter(this)
        binding.close.setOnClickListener(this)
        binding.voiceBtn.setOnClickListener(this)
        binding.callBtn.setOnClickListener(this)

        binding.rbChat.setOnClickListener {
            tabPosition = 0
            onTabChanged()
        }
        binding.rbCharge.setOnClickListener {
            tabPosition = 1
            onTabChanged()
        }
        return binding.root
    }

    /**
     * 切换左边栏顶部按钮
     */
    private fun onTabChanged() {
        binding.viewPager.setCurrentItem(tabPosition, false)
        if (currentFragment is ChargeChatFragment || currentFragment is ChatFragment) {
            hideLeftButton(false)
            currentFragment?.switchInputView(binding.voiceBtn.isSelected)
        } else {
            hideLeftButton(true)
        }
    }

    /**
     * 显示或隐藏左边栏底部按钮
     */
    fun hideLeftButton(hidden: Boolean) {
        binding.callBtn.visibility = if (hidden) View.GONE else View.VISIBLE
        binding.voiceBtn.visibility = if (hidden) View.GONE else View.VISIBLE
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.close -> dismiss()
            R.id.voice_btn -> {
                binding.voiceBtn.isSelected = !binding.voiceBtn.isSelected
                currentFragment?.switchInputView(binding.voiceBtn.isSelected)
            }
            R.id.call_btn -> Msg.toast("暂未开放此功能")
        }
    }

    override fun onStart() {
        super.onStart()
        // 将对话框的大小按屏幕大小的百分比设置
        dialog?.window?.let { _window ->
            val lp = _window.attributes
            lp.width = (max(screen.x, screen.y) * 0.6).toInt() //设置宽度
            lp.height = (min(screen.x, screen.y) * 0.9).toInt() //设置宽度
            _window.attributes = lp
            _window.setGravity(Gravity.LEFT)
            _window.setWindowAnimations(R.style.sister_DialogWindow)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!PermissionUtils.checkPermission(requireActivity())) {
            PermissionUtils.requestPermission(
                requireActivity(),
                object : OnPermissionResult {
                    override fun permissionResult(isOpen: Boolean) {
                    }
                }
            )
        }
        Bubble.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (PermissionUtils.checkPermission(AndroidX.myApp) && SisterX.hasSister()) {
            Bubble.show()
        }
    }

    override fun onBusEvent(event: Pair<Int, Any>) {
        if (event.first == BUZ_LOGOUT_MSG) {
            AlertDialog.Builder(requireActivity())
                .setTitle("温馨提示")
                .setMessage(event.second as String)
                .setCancelable(false)
                .setPositiveButton("确定") { dialog, _ ->
                    dialog.dismiss()
                    dismissAllowingStateLoss()
                }
                .create()
                .show()
            return
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(cancelable: Boolean): MainDialogFragment {
            return MainDialogFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("cancelable", cancelable)
                }
            }
        }
    }

    private class MyAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun createFragment(position: Int): Fragment {
            if (0 == position) {
                return ChatFragment.newInstance()
            }
            return ChargeFragment.newInstance()
        }

        override fun getItemCount(): Int {
            return 2
        }
    }
}