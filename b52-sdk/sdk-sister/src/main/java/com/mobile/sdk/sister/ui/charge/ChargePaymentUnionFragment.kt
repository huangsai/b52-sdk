package com.mobile.sdk.sister.ui.charge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.guava.android.context.putTextToClipboard
import com.mobile.guava.android.mvvm.BaseFragment
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterFragmentChargePaymentUnionBinding

/**
 * 银行卡收款页面
 */
class ChargePaymentUnionFragment : BaseFragment(), View.OnClickListener {

    private var _binding: SisterFragmentChargePaymentUnionBinding? = null
    private val binding: SisterFragmentChargePaymentUnionBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentChargePaymentUnionBinding.inflate(inflater, container, false)
        binding.copyName.setOnClickListener(this)
        binding.copyNum.setOnClickListener(this)
        binding.copyAddress.setOnClickListener(this)
        binding.copyBranch.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.copy_name -> requireContext().putTextToClipboard(binding.name.text.toString())
            R.id.copy_num -> requireContext().putTextToClipboard(binding.num.text.toString())
            R.id.copy_address -> requireContext().putTextToClipboard(binding.address.text.toString())
            R.id.copy_branch -> requireContext().putTextToClipboard(binding.branch.text.toString())
        }
        ToastPop(requireParentFragment()).show("复制成功")
    }

    companion object {

        @JvmStatic
        fun newInstance(): ChargePaymentUnionFragment = ChargePaymentUnionFragment()
    }

}