package com.mobile.sdk.sister.ui

import android.Manifest
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mobile.guava.android.mvvm.BaseAppCompatDialogFragment
import com.mobile.guava.android.mvvm.Msg
import com.mobile.guava.android.ui.screen.screen
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.databinding.SisterDialogMainBinding
import com.mobile.sdk.sister.ui.chat.ChatFragment
import com.mobile.sdk.sister.ui.system.SystemFragment

class MainDialogFragment : BaseAppCompatDialogFragment(), RadioGroup.OnCheckedChangeListener,
    View.OnClickListener {

    private val permissionsContract = RequestMultiplePermissions()

    private var _binding: SisterDialogMainBinding? = null
    private val binding: SisterDialogMainBinding get() = _binding!!

    private var sCancelable: Boolean = false

    val model: SisterViewModel by viewModels { SisterX.component.viewModelFactory() }
    var currentFragment: TopMainFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyDialogCount = true
        arguments?.let {
            sCancelable = it.getBoolean("cancelable")
        }
        requestPermission()
    }

    private fun requestPermission() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )
        registerForActivityResult(permissionsContract) {
            var hasNeededPermission = true
            for ((key, value) in it.entries) {
                if (!value) {
                    when (key) {
                        Manifest.permission.CAMERA -> {
                            hasNeededPermission = false
                        }
                        Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                            hasNeededPermission = false
                        }
                        Manifest.permission.RECORD_AUDIO -> {
                            hasNeededPermission = false
                        }
                    }
                }
            }
            if (!hasNeededPermission) {
                Msg.toast(R.string.sister_pls_request_permission)
                this.dismissAllowingStateLoss()
            }
        }.launch(permissions)

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
        binding.mainRg.setOnCheckedChangeListener(this)
        binding.close.setOnClickListener(this)
        binding.voiceBtn.setOnClickListener(this)
        binding.callBtn.setOnClickListener(this)
        return binding.root
    }

    /**
     * 加载红点提示
     */
    private fun loadPoint() {

    }

    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        val pos = when (checkedId) {
            R.id.rb_system -> {
                binding.callBtn.visibility = View.GONE
                binding.voiceBtn.visibility = View.GONE
                1
            }
            else -> {
                binding.callBtn.visibility = View.VISIBLE
                binding.voiceBtn.visibility = View.VISIBLE
                0
            }
        }
        binding.viewPager.setCurrentItem(pos, false)
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
            lp.width = (screen.x * 0.65).toInt() //设置宽度
            lp.height = (screen.y * 0.9).toInt() //设置宽度
            _window.attributes = lp
            _window.setGravity(Gravity.LEFT)
            _window.setWindowAnimations(R.style.sister_DialogWindow)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            return SystemFragment.newInstance()
        }

        override fun getItemCount(): Int {
            return 2
        }
    }

}