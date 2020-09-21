package com.mobile.sdk.sister.ui.items

import android.net.Uri
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterItemViewerImageBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase

class ViewerImageItem(val data: Uri) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(SisterItemViewerImageBinding::bind)
        binding.imgSrc.displayType = ImageViewTouchBase.DisplayType.FIT_HEIGHT
        holder.attachImageLoader(R.id.img_src)
    }

    override fun unbind(holder: AdapterViewHolder) {
    }

    override fun getLayout(): Int {
        return R.layout.sister_item_viewer_image
    }
}