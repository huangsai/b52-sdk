package com.mobile.sdk.sister.ui.charge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.guava.android.mvvm.BaseFragment
import com.mobile.sdk.sister.databinding.SisterFragmentChargeReceiveWechatBinding

/**
 * 微信收款码页面
 */
class ChargeReceiveWechatFragment : BaseFragment() {

    private var _binding: SisterFragmentChargeReceiveWechatBinding? = null
    private val binding: SisterFragmentChargeReceiveWechatBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentChargeReceiveWechatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    companion object {

        @JvmStatic
        fun newInstance(): ChargeReceiveWechatFragment = ChargeReceiveWechatFragment()
    }
}