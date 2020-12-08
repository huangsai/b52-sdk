package com.mobile.sdk.sister.ui.charge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.http.TYPE_DEPOSIT_ALIPAY
import com.mobile.sdk.sister.data.http.TYPE_DEPOSIT_UNION
import com.mobile.sdk.sister.data.http.TYPE_DEPOSIT_WECHAT
import com.mobile.sdk.sister.databinding.SisterFragmentChargePaymentBinding
import com.mobile.sdk.sister.ui.TopMainFragment

/**
 * 充值收款页面
 */
class ChargePaymentFragment : TopMainFragment() {

    private var _binding: SisterFragmentChargePaymentBinding? = null
    private val binding: SisterFragmentChargePaymentBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentChargePaymentBinding.inflate(inflater, container, false)
        binding.chatBack.setOnClickListener { parentFragmentManager.popBackStack() }
        var type: Int? = null
        arguments?.let { type = it.getInt("type") }
        val fragment = when (type) {
            TYPE_DEPOSIT_UNION -> ChargePaymentUnionFragment.newInstance()
            TYPE_DEPOSIT_WECHAT -> ChargePaymentWechatFragment.newInstance()
            TYPE_DEPOSIT_ALIPAY -> ChargePaymentAlipayFragment.newInstance()
            else -> throw IllegalStateException()
        }
        childFragmentManager.commit {
            disallowAddToBackStack().add(
                R.id.container_payment, fragment, fragment.javaClass.simpleName
            )
        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(type: Int): ChargePaymentFragment = ChargePaymentFragment().apply {
            arguments = Bundle().apply {
                putInt("type", type)
            }
        }
    }
}