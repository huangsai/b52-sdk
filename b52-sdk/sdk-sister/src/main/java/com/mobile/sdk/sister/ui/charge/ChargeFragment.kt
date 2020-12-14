package com.mobile.sdk.sister.ui.charge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterFragmentChargeBinding
import com.mobile.sdk.sister.ui.TopMainFragment

class ChargeFragment : TopMainFragment() {

    private var _binding: SisterFragmentChargeBinding? = null
    private val binding: SisterFragmentChargeBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentChargeBinding.inflate(inflater, container, false)
        childFragmentManager.commit {
            disallowAddToBackStack()
            replace(
                R.id.layout_fragment,
                ChargeItemFragment.newInstance(),
                ChargeItemFragment.javaClass.simpleName
            )
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance(): ChargeFragment = ChargeFragment()
    }
}