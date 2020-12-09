package com.mobile.sdk.sister.ui.charge

import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mobile.sdk.sister.R
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.createBalloon
import com.skydoves.balloon.overlay.BalloonOverlayAnimation

/**
 * 弹框提示文字
 */
class ToastPop(private val fragment: Fragment) {

    private val balloon: Balloon = createBalloon(fragment.requireContext()) {
        setLayout(R.layout.sister_popup_toast)
        cornerRadius = 0f
        arrowVisible = false
        setBackgroundDrawableResource(R.drawable.sister_press_voice_popup_bg)
        setBalloonOverlayAnimation(BalloonOverlayAnimation.FADE)
        setLifecycleOwner(fragment)
    }

    fun show(content: String) {
        balloon.getContentView().findViewById<TextView>(R.id.text_content).text = content
        val rootView = fragment.requireView()
        balloon.showAlignRight(
            rootView,
            0,
            0
        )
        rootView.postDelayed({ balloon.dismiss() }, 2000)
    }

}