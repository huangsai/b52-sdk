package com.mobile.sdk.sister.ui.charge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.guava.android.mvvm.BaseFragment
import com.mobile.sdk.sister.databinding.SisterFragmentChargePaymentAlipayBinding

/**
 * 支付宝收款页面
 */
class ChargePaymentAlipayFragment : BaseFragment() {

    private var _binding: SisterFragmentChargePaymentAlipayBinding? = null
    private val binding: SisterFragmentChargePaymentAlipayBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentChargePaymentAlipayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    companion object {

        @JvmStatic
        fun newInstance(): ChargePaymentAlipayFragment = ChargePaymentAlipayFragment()
    }
}