package com.mobile.sdk.sister.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.mobile.guava.android.mvvm.BaseAppCompatDialogFragment
import com.mobile.guava.android.mvvm.BaseFragment
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.databinding.SisterFragmentDialogBinding

class SisterDialogFragment : BaseAppCompatDialogFragment() {

    private var _binding: SisterFragmentDialogBinding? = null
    private val binding: SisterFragmentDialogBinding get() = _binding!!
    private var sCancelable = false

    var fragment: MyFragment? = null
        private set

    private val viewModel: SisterViewModel by viewModels {
        SisterX.component.viewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.sister_DialogFragment)
        arguments?.let {
            sCancelable = it.getBoolean("cancelable")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentDialogBinding.inflate(inflater, container, false)
        isCancelable = sCancelable
        binding.imgClose.setOnClickListener {
            dismissAllowingStateLoss()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { _window ->
            _window.setGravity(Gravity.LEFT)
            _window.setWindowAnimations(R.style.sister_DialogWindow)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadSession() {
    }

    fun postText() {
    }

    fun postImage() {
    }

    fun postAudio() {
    }

    fun requestSister() {
    }

    abstract class MyFragment : BaseFragment() {

        protected val pFragment: SisterDialogFragment by lazy {
            requireParentFragment() as SisterDialogFragment
        }

        protected val viewModel: SisterViewModel get() = pFragment.viewModel

        override fun onResume() {
            super.onResume()
            pFragment.fragment = this
        }

        override fun onDestroy() {
            super.onDestroy()
            if (pFragment.fragment == this) {
                pFragment.fragment = null
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(cancelable: Boolean): SisterDialogFragment {
            return SisterDialogFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("cancelable", cancelable)
                }
            }
        }
    }
}