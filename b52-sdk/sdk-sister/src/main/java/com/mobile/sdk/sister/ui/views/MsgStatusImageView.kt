package com.mobile.sdk.sister.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatImageView
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.data.http.STATUS_MSG_FAILED
import com.mobile.sdk.sister.data.http.STATUS_MSG_PROCESSING
import com.mobile.sdk.sister.data.http.STATUS_MSG_SUCCESS

class MsgStatusImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var _status = -1

    var status: Int
        get() = _status
        set(value) {
            _status = value
            when (value) {
                STATUS_MSG_SUCCESS -> {
                    this.clearAnimation()
                    this.visibility = GONE
                }
                STATUS_MSG_FAILED -> {
                    this.clearAnimation()
                    this.visibility = VISIBLE
                    setImageResource(R.drawable.sister_icon_fail)
                }
                STATUS_MSG_PROCESSING -> {
                    this.visibility = VISIBLE
                    setImageResource(R.drawable.sister_icon_sending)
                    val rotateAnimation =
                        AnimationUtils.loadAnimation(context, R.anim.sister_anim_rotate)
                    rotateAnimation.interpolator = LinearInterpolator()
                    this.startAnimation(rotateAnimation)
                }
            }
        }
}