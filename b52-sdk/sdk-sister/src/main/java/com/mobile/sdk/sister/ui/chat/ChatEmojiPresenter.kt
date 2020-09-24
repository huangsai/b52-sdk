package com.mobile.sdk.sister.ui.chat

import android.view.View
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.EmojiHandle
import com.mobile.sdk.sister.ui.SisterViewModel
import com.mobile.sdk.sister.ui.items.ViewerEmojiItem
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.createBalloon

class ChatEmojiPresenter(
    fragment: ChatFragment,
    binding: SisterFragmentChatBinding,
    model: SisterViewModel,
) : BaseChatPresenter(fragment, binding, model) {

    private var balloon: Balloon? = null
    private lateinit var viewPager: ViewPager2

    private val adapter = RecyclerAdapter()

    fun showPop() {
        if (balloon != null) {
            balloon!!.showAlignTop(binding.layoutInput)
            return
        }
        val popWidth = binding.layoutInput.width
        balloon = createBalloon(fragment.requireContext()) {
            setLayout(R.layout.sister_popup_chat_emoji)
            cornerRadius = 10f
            arrowVisible = false
            width = popWidth - 15
            setBackgroundColorResource(R.color.sister_color_black_transparent)
        }
        balloon!!.showAlignTop(binding.layoutInput)
        viewPager = balloon!!.getContentView().findViewById(R.id.view_pager)
        val tabLayout = balloon!!.getContentView().findViewById<TabLayout>(R.id.tab_layout)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
        load()
    }

    override fun load() {
        val items = listOf(
            ViewerEmojiItem(EmojiHandle.emojiInfos.subList(0, 26)),
            ViewerEmojiItem(EmojiHandle.emojiInfos.subList(0, 26)),
            ViewerEmojiItem(EmojiHandle.emojiInfos.subList(0, 26))
        )
        adapter.addAll(items)
    }

    override fun onClick(v: View?) {
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        viewPager.adapter = null
    }

    override fun onDestroy() {
    }

}