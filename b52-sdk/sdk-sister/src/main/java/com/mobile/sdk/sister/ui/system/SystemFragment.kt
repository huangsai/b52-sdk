package com.mobile.sdk.sister.ui.system

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.http.ApiSimpleMessage
import com.mobile.sdk.sister.data.http.TYPE_SYSTEM
import com.mobile.sdk.sister.databinding.SisterFragmentSystemBinding
import com.mobile.sdk.sister.ui.TopMainFragment
import com.mobile.sdk.sister.ui.asNormal
import com.mobile.sdk.sister.ui.items.MsgItem
import com.pacific.adapter.RecyclerAdapter

class SystemFragment : TopMainFragment() {

    private var _binding: SisterFragmentSystemBinding? = null
    private val binding: SisterFragmentSystemBinding get() = _binding!!

    private val adapter = RecyclerAdapter()

    companion object {
        @JvmStatic
        fun newInstance(): SystemFragment = SystemFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentSystemBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.addItemDecoration(
            LinearItemDecoration.builder(requireContext())
                .color(android.R.color.transparent, R.dimen.size_10dp)
                .build()
        )
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val items = listOf(
            ApiSimpleMessage("0", TYPE_SYSTEM, "系统通知", 12323123).asNormal(),
            ApiSimpleMessage(
                "", TYPE_SYSTEM, "系统通知系统通知系统通知", 12323123).asNormal(),
            ApiSimpleMessage("", TYPE_SYSTEM, "系统通知系统通知系统通知系统通知系统通知", 12323123).asNormal(),
            ApiSimpleMessage("", TYPE_SYSTEM, "系统通知系统通知", 12323123).asNormal(),
            ApiSimpleMessage("", TYPE_SYSTEM, "系统通知系统通知系统通知系统通知系统通知系统通知系统通知", 12323123).asNormal(),
        ).map {
            MsgItem.create(it)
        }
        adapter.addAll(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _binding = null
    }
}