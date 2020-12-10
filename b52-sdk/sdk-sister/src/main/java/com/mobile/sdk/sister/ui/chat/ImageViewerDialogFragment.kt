package com.mobile.sdk.sister.ui.chat

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.mobile.ext.glide.GlideApp
import com.mobile.guava.android.mvvm.BaseAppCompatDialogFragment
import com.mobile.guava.data.Values
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterFragmentImageViewerDialogBinding
import com.mobile.sdk.sister.ui.items.ViewerImageItem
import com.pacific.adapter.AdapterImageLoader
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter

/**
 * 查看大图
 */
class ImageViewerDialogFragment : BaseAppCompatDialogFragment(), AdapterImageLoader,
    View.OnClickListener {

    private var _binding: SisterFragmentImageViewerDialogBinding? = null
    private val binding get() = _binding!!
    private val adapter = RecyclerAdapter()

    private var list: List<Uri> = emptyList()
    private var position = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.sister_FullScreenDialog)
        list = Values.take("ImageViewerDialogFragment")!!
        position = Values.take("ImageViewerDialogFragment_position")!!
        adapter.imageLoader = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SisterFragmentImageViewerDialogBinding.inflate(inflater, container, false)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.txtIndex.text = "${position + 1}/${adapter.itemCount}"
            }
        })
        binding.viewPager.adapter = adapter
        binding.imgClose.setOnClickListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.adapter = null
        _binding = null
    }

    private fun load() {
        val items = list.map { ViewerImageItem(it) }
        adapter.addAll(items)
        binding.viewPager.setCurrentItem(position, false)
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        val item = holder.item<ViewerImageItem>()
        GlideApp.with(this)
            .asBitmap()
            .load(item.data)
            .into(view)
    }

    override fun onClick(v: View?) {
        dismiss()
    }

    companion object {

        @JvmStatic
        fun newInstance(
            list: List<Uri>,
            position: Int
        ): ImageViewerDialogFragment = ImageViewerDialogFragment().apply {
            Values.put("ImageViewerDialogFragment", list)
            Values.put("ImageViewerDialogFragment_position", position)
        }
    }
}