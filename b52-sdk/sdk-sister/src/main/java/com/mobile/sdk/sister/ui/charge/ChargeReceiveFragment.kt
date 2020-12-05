package com.mobile.sdk.sister.ui.charge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterFragmentChargeReceiveBinding
import com.mobile.sdk.sister.ui.TopMainFragment

/**
 * 充值收款页面
 */
class ChargeReceiveFragment : TopMainFragment() {

    private var _binding: SisterFragmentChargeReceiveBinding? = null
    private val binding: SisterFragmentChargeReceiveBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentChargeReceiveBinding.inflate(inflater, container, false)
        binding.chatBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        childFragmentManager.commit {
            this.disallowAddToBackStack()
                .replace(
                    R.id.layout_fragment,
                    ChargeReceiveWechatFragment.newInstance(),
                    ChargeReceiveWechatFragment.newInstance().javaClass.simpleName
                )
        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(): ChargeReceiveFragment = ChargeReceiveFragment()
    }
}