package com.mobile.sdk.sister.ui.views

import android.app.Dialog
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.mobile.guava.android.context.dp2px
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.android.mvvm.AppManager
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.bubble.Bubble
import kotlin.math.roundToInt

object MyKeyboardHelper : Bubble.Callback {

    private var neededRecoverFloatWindow = false

    init {
        SisterX.hasBufferMsgItems.observeForever {
            Bubble.setBadge(SisterX.bufferMsgItems.isNotEmpty() && Bubble.isShowing)
        }
        AndroidX.isAppInForeground.observeForever {
            if (true == it) {
                if (neededRecoverFloatWindow) {
                    neededRecoverFloatWindow = false
                    Bubble.show()
                }
            } else {
                if (Bubble.isShowing) {
                    neededRecoverFloatWindow = true
                    Bubble.hide()
                }
            }
        }

        Bubble.callback = this
    }

    fun isKeyboardVisible(dialog: Dialog): Boolean {
        val r = Rect()
        val activityRoot = dialog.window!!.decorView
        val visibleThreshold = dialog.context.dp2px(100f).roundToInt()
        activityRoot.getWindowVisibleDisplayFrame(r)
        val heightDiff = activityRoot.rootView.height - r.height()
        return heightDiff > visibleThreshold
    }

    override fun onTouch(view: View, event: MotionEvent) {
    }

    override fun onViewCreated(view: View) {
        view.setOnClickListener {
            AppManager.currentActivity()?.let { activity ->
                if (activity is FragmentActivity) {
                    SisterX.show(activity, false)
                }
            }
        }
    }

    override fun onHide(view: View) {
    }

    override fun onShow(view: View) {
    }

    override fun onDismiss() {
    }
}