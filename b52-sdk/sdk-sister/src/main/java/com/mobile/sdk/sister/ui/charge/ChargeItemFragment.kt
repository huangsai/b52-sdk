package com.mobile.sdk.sister.ui.charge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.http.ApiCharge
import com.mobile.sdk.sister.databinding.SisterFragmentChargeItemBinding
import com.mobile.sdk.sister.ui.TopMainFragment
import com.mobile.sdk.sister.ui.items.ChargeItem
import com.pacific.adapter.RecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 一级充值会话条目页面
 */
class ChargeItemFragment : TopMainFragment(), View.OnClickListener {

    private var _binding: SisterFragmentChargeItemBinding? = null
    private val binding: SisterFragmentChargeItemBinding get() = _binding!!

    private val adapter = RecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentChargeItemBinding.inflate(inflater, container, false)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.addItemDecoration(
            LinearItemDecoration.builder(requireContext())
                .color(android.R.color.transparent, R.dimen.size_7dp)
                .build()
        )
        binding.recycler.adapter = adapter
        adapter.onClickListener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        load()
    }

    private fun load() {
        val data = listOf(
            ApiCharge(1), ApiCharge(2), ApiCharge(3),
            ApiCharge(4), ApiCharge(5), ApiCharge(6)
        )
        lifecycleScope.launch(Dispatchers.IO) {
            val items = data.map {
                ChargeItem(it)
            }
            withContext(Dispatchers.Main) {
                adapter.addAll(items)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recycler.adapter = null
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.root -> {
                parentFragmentManager.commit {
                    this.addToBackStack(null)
                        .replace(
                            R.id.layout_fragment,
                            ChargeChatFragment.newInstance(),
                            ChargeChatFragment.javaClass.simpleName
                        )
                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(): ChargeItemFragment = ChargeItemFragment()
    }
}