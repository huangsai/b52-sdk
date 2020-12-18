package com.mobile.sdk.sister.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterFragmentSessionBinding
import com.mobile.sdk.sister.ui.SisterDialogFragment
import com.mobile.sdk.sister.ui.items.SessionItem
import com.pacific.adapter.RecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SessionFragment : SisterDialogFragment.MyFragment() {

    private var _binding: SisterFragmentSessionBinding? = null
    private val binding: SisterFragmentSessionBinding get() = _binding!!

    private val adapter = RecyclerAdapter()
    private val adapterOnClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.item_session -> {
                val sFragment = ChatFragment2.newInstance()
                val tag = sFragment.javaClass.simpleName
                childFragmentManager.commit {
                    replace(R.id.layout_fragment, sFragment, tag)
                    addToBackStack(tag)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentSessionBinding.inflate(inflater, container, false)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.addItemDecoration(
            LinearItemDecoration.builder(requireContext())
                .color(android.R.color.transparent, R.dimen.size_7dp)
                .hideLastDecoration()
                .build()
        )
        binding.recycler.adapter = adapter
        adapter.onClickListener = adapterOnClickListener
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        pFragment.setFlag(3)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recycler.adapter = null
        _binding = null
    }

    override fun load() {
        lifecycleScope.launch(Dispatchers.IO) {
            val items = viewModel.loadChargeChatList().map {
                SessionItem(it)
            }
            withContext(Dispatchers.Main) {
                adapter.addAll(items)
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(): SessionFragment = SessionFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}