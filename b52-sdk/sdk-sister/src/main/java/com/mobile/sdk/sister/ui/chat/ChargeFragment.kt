package com.mobile.sdk.sister.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterFragmentChargeBinding
import com.mobile.sdk.sister.ui.TopMainFragment
import com.mobile.sdk.sister.ui.items.NoticeItem
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        load()
    }

    private fun load() {
        lifecycleScope.launch(Dispatchers.IO) {
            val items = fParent.model.loadSystemNotices().map {
                NoticeItem(it)
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
    }

    companion object {

        @JvmStatic
        fun newInstance(): ChargeFragment = ChargeFragment()
    }
}