package com.mobile.sdk.sister.ui.views

import android.app.Dialog
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import com.lzf.easyfloat.interfaces.OnFloatCallbacks
import com.mobile.guava.android.context.dp2px
import kotlin.math.roundToInt

object MyKeyboardHelper:OnFloatCallbacks {
    
    fun isKeyboardVisible(dialog: Dialog): Boolean {
        val r = Rect()
        val activityRoot = dialog.window!!.decorView
        val visibleThreshold = dialog.context.dp2px(100f).roundToInt()
        activityRoot.getWindowVisibleDisplayFrame(r)
        val heightDiff = activityRoot.rootView.height - r.height()
        return heightDiff > visibleThreshold
    }

    override fun createdResult(isCreated: Boolean, msg: String?, view: View?) {
    }

    override fun dismiss() {
    }

    override fun drag(view: View, event: MotionEvent) {
    }

    override fun dragEnd(view: View) {
    }

    override fun hide(view: View) {
    }

    override fun show(view: View) {
    }

    override fun touchEvent(view: View, event: MotionEvent) {
    }
}