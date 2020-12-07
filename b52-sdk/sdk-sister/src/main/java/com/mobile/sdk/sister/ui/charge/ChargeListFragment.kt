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
import com.mobile.sdk.sister.databinding.SisterFragmentChargeListBinding
import com.mobile.sdk.sister.ui.TopMainFragment
import com.mobile.sdk.sister.ui.items.ChargeItem
import com.pacific.adapter.RecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 充值会话列表页面
 */
class ChargeListFragment : TopMainFragment(), View.OnClickListener {

    private var _binding: SisterFragmentChargeListBinding? = null
    private val binding: SisterFragmentChargeListBinding get() = _binding!!

    private val adapter = RecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentChargeListBinding.inflate(inflater, container, false)
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
        lifecycleScope.launch(Dispatchers.IO) {
            val items = fParent.model.loadChargeChatList().map {
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
                    addToBackStack(null).hide(this@ChargeListFragment).add(
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
        fun newInstance(): ChargeListFragment = ChargeListFragment()
    }
}