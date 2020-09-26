package com.mobile.sdk.sister.ui.views

import android.app.Dialog
import android.graphics.Rect
import com.mobile.guava.android.context.dp2px
import kotlin.math.roundToInt

object MyKeyboardHelper {
    fun isKeyboardVisible(dialog: Dialog): Boolean {
        val r = Rect()
        val activityRoot = dialog.window!!.decorView
        val visibleThreshold = dialog.context.dp2px(100f).roundToInt()
        activityRoot.getWindowVisibleDisplayFrame(r)
        val heightDiff = activityRoot.rootView.height - r.height()
        return heightDiff > visibleThreshold
    }
}