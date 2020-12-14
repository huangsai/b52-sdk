package com.mobile.sdk.sister.ui.charge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.guava.android.context.putTextToClipboard
import com.mobile.guava.android.mvvm.BaseFragment
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterFragmentChargePaymentAlipayBinding

/**
 * 支付宝收款页面
 */
class ChargePaymentAlipayFragment : BaseFragment(), View.OnClickListener {

    private var _binding: SisterFragmentChargePaymentAlipayBinding? = null
    private val binding: SisterFragmentChargePaymentAlipayBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentChargePaymentAlipayBinding.inflate(inflater, container, false)
        binding.copyAccount.setOnClickListener(this)
        binding.copyFirstName.setOnClickListener(this)
        binding.copyLastName.setOnClickListener(this)
        binding.copyName.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.copy_account -> requireContext().putTextToClipboard(binding.account.text.toString())
            R.id.copy_first_name -> requireContext().putTextToClipboard(binding.firstName.text.toString())
            R.id.copy_last_name -> requireContext().putTextToClipboard(binding.lastName.text.toString())
            R.id.copy_name -> requireContext().putTextToClipboard(binding.name.text.toString())
        }
        ToastPop(requireParentFragment()).show("复制成功")
    }

    companion object {

        @JvmStatic
        fun newInstance(): ChargePaymentAlipayFragment = ChargePaymentAlipayFragment()
    }

}