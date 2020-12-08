package com.mobile.sdk.sister.ui.charge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.guava.android.mvvm.BaseFragment
import com.mobile.sdk.sister.databinding.SisterFragmentChargePaymentUnionBinding

/**
 * 银行卡收款页面
 */
class ChargePaymentUnionFragment : BaseFragment() {

    private var _binding: SisterFragmentChargePaymentUnionBinding? = null
    private val binding: SisterFragmentChargePaymentUnionBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentChargePaymentUnionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    companion object {

        @JvmStatic
        fun newInstance(): ChargePaymentUnionFragment = ChargePaymentUnionFragment()
    }
}