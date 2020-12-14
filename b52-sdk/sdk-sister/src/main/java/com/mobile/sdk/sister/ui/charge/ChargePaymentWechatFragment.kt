package com.mobile.sdk.sister.ui.charge

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.guava.android.mvvm.BaseFragment
import com.mobile.sdk.sister.databinding.SisterFragmentChargePaymentWechatBinding

/**
 * 微信收款码页面
 */
class ChargePaymentWechatFragment : BaseFragment() {

    private var _binding: SisterFragmentChargePaymentWechatBinding? = null
    private val binding: SisterFragmentChargePaymentWechatBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentChargePaymentWechatBinding.inflate(inflater, container, false)
        binding.savePic.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        binding.savePic.paint.isAntiAlias = true
        binding.savePic.setOnClickListener {
            //保存图片
            ToastPop(requireParentFragment()).show("保存成功")
        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(): ChargePaymentWechatFragment = ChargePaymentWechatFragment()
    }
}