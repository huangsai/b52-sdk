package com.mobile.sdk.sister.ui.charge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.http.ApiCharge
import com.mobile.sdk.sister.databinding.SisterFragmentChargeBinding
import com.mobile.sdk.sister.ui.TopMainFragment
import com.mobile.sdk.sister.ui.chat.ChatFragment
import com.mobile.sdk.sister.ui.items.ChargeItem
import com.pacific.adapter.RecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChargeFragment : TopMainFragment(), View.OnClickListener {

    private var _binding: SisterFragmentChargeBinding? = null
    private val binding: SisterFragmentChargeBinding get() = _binding!!

    private val adapter = RecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentChargeBinding.inflate(inflater, container, false)
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
                fParent.addFragment(R.id.layout_fragment, ChatFragment.newInstance(true))
                binding.recycler.visibility = View.GONE
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(): ChargeFragment = ChargeFragment()
    }
}