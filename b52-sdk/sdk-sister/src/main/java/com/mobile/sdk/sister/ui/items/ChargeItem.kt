package com.mobile.sdk.sister.ui.items

import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.http.ApiCharge
import com.mobile.sdk.sister.databinding.SisterItemChargeBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

/**
 * 充值Item
 */
class ChargeItem(val data: ApiCharge) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(SisterItemChargeBinding::bind)
        holder.attachOnClickListener(R.id.root)
    }

    override fun getLayout(): Int {
        return R.layout.sister_item_charge
    }
}