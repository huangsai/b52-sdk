package com.mobile.sdk.sister.ui.chat

import android.view.View
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.SisterDialogFragment
import com.mobile.sdk.sister.ui.SisterViewModel
import com.mobile.sdk.sister.ui.chat.EmotionHandle.splitEmotionInfo
import com.mobile.sdk.sister.ui.items.ViewerEmotionItem
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.createBalloon
import com.skydoves.balloon.overlay.BalloonOverlayAnimation

/**
 * 处理表情业务逻辑代码
 */
class ChatEmotionPresenter(
    fragment: SisterDialogFragment.MyFragment,
    binding: SisterFragmentChatBinding,
    model: SisterViewModel,
) : BasePresenter(fragment, binding, model) {

    private var balloon: Balloon? = null
    private var viewPager: ViewPager2? = null
    private var emotionDelete: ImageView? = null
    private var emotionSend: ImageView? = null

    private val adapter = RecyclerAdapter()

    fun showPop() {
        if (balloon != null) {
            balloon!!.showAlignTop(binding.layoutInput)
            return
        }
        val popWidth = binding.layoutInput.width
        //创建表情弹窗
        balloon = createBalloon(fragment.requireContext()) {
            setLayout(R.layout.sister_popup_chat_emotion)
            cornerRadius = 10f
            arrowVisible = false
            width = popWidth
            marginLeft = 10
            marginRight = 10
            setBackgroundColorResource(R.color.sister_color_black_transparent)
            setElevation(0)
            setBalloonOverlayAnimation(BalloonOverlayAnimation.FADE)
            setLifecycleOwner(fragment)
        }
        balloon!!.showAlignTop(binding.layoutInput)
        emotionDelete = balloon!!.getContentView().findViewById(R.id.emotion_delete)
        emotionSend = balloon!!.getContentView().findViewById(R.id.emotion_send)
        emotionDelete!!.setOnClickListener(this)
        emotionSend!!.setOnClickListener(this)
        updateButtonStatus()
        viewPager = balloon!!.getContentView().findViewById(R.id.view_pager)
        val tabLayout = balloon!!.getContentView().findViewById<TabLayout>(R.id.tab_layout)
        viewPager!!.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager!!) { _, _ -> }.attach()
        load()
    }

    override fun load() {
        val data = splitEmotionInfo()
        val items = data.map {
            ViewerEmotionItem(it, binding)
        }
        adapter.addAll(items)
    }

    /**
     * 更新删除、发送按钮状态
     */
    fun updateButtonStatus() {
        val isSelected = binding.chatEt.text.toString().trim().isNotEmpty()
        emotionSend?.isSelected = isSelected
        emotionDelete?.isSelected = isSelected
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.emotion_delete -> {
                if (!v.isSelected) return
                EmotionHandle.deleteEmotionText(binding.chatEt)
            }
            R.id.emotion_send -> {
                if (!v.isSelected) return
                fragment.chatListPresenter.postText(binding.chatEt.text.toString().trim())
                balloon?.dismiss()
            }
        }
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
    }

    override fun onDestroyView() {
        viewPager?.adapter = null
    }

    override fun onDestroy() {
    }
}