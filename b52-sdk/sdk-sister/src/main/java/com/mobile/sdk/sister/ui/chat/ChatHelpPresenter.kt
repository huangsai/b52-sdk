package com.mobile.sdk.sister.ui.chat

import android.view.View
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.guava.android.mvvm.Msg
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.mobile.guava.jvm.domain.Source
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.SisterDialogFragment
import com.mobile.sdk.sister.ui.SisterViewModel
import com.mobile.sdk.sister.ui.items.HelpItem
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 系统回复业务逻辑代码
 */
class ChatHelpPresenter(
    fragment: SisterDialogFragment.MyFragment,
    binding: SisterFragmentChatBinding,
    model: SisterViewModel,
    private val isCharge: Boolean
) : BasePresenter(fragment, binding, model) {

    private val adapter = RecyclerAdapter()

    init {
        binding.recyclerHelp.layoutManager = LinearLayoutManager(
            fragment.requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.recyclerHelp.addItemDecoration(
            LinearItemDecoration.builder(fragment.requireContext())
                .color(android.R.color.transparent, R.dimen.size_5dp)
                .horizontal()
                .build()
        )
        binding.recyclerHelp.adapter = adapter
        adapter.onClickListener = this
    }

    override fun load() {
        if (!adapter.isEmpty()) {
            return
        }

        if (isCharge) {
            requestChargeSysReply()
        } else {
            requestSisterSysReply()
        }
    }

    /**
     * 请求充值会话系统回复
     */
    private fun requestChargeSysReply() {

    }

    /**
     * 请求充值会话系统回复
     */
    private fun requestSisterSysReply() {
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            val source = model.getSysReply()
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        val items = source.requireData().map {
                            HelpItem(it)
                        }
                        adapter.addAll(items)
                    }
                    is Source.Error -> {
                        Msg.toast(source.requireError().message!!)
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.item_help_tag -> {
                model.postSysReply(true, AdapterUtils.getHolder(v).item<HelpItem>().data)
            }
        }
    }

    override fun onDestroyView() {
        binding.recyclerHelp.adapter = null
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        TODO("Not yet implemented")
    }
}