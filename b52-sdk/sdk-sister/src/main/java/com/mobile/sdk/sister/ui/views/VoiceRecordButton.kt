package com.mobile.sdk.sister.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView

class VoiceRecordButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var mDownTime: Long = 0L
    private var mCallback: RecordButtonTouchCallback? = null

    private fun isTouchPointInView(xAxis: Float, yAxis: Float): Boolean {
        val location = IntArray(2)
        getLocationOnScreen(location)
        val left = location[0]
        val top = location[1]
        val right = left + this.measuredWidth
        val bottom = top + this.measuredHeight
        return yAxis >= top && yAxis <= bottom && xAxis >= left && xAxis <= right
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val rawX: Float
        val rawY: Float
        if (action == MotionEvent.ACTION_DOWN) {
            mDownTime = System.currentTimeMillis()
            //回调外部，决定是否拦截
            if (mCallback?.isIntercept()!!) {
                return super.dispatchTouchEvent(event)
            }
            mCallback?.let {
                it.onStartAudio()
                it.onRestoreNormalTouchArea()
            }
            return true
        } else if (action == MotionEvent.ACTION_MOVE) {
            rawX = event.rawX
            rawY = event.rawY
            if (isTouchPointInView(rawX, rawY)) {
                mCallback?.onRestoreNormalTouchArea()
            } else {
                mCallback?.onTouchCancelArea()
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            rawX = event.rawX
            rawY = event.rawY
            val intervalTime = System.currentTimeMillis() - mDownTime
            //不够最短时间
            when {
                intervalTime <= MIN_INTERVAL_TIME -> {
                    mCallback?.onTouchIntervalTimeSmall()
                }
                isTouchPointInView(rawX, rawY) -> {
                    mCallback?.onFinish()
                }
                else -> {
                    mCallback?.onCancel()
                }
            }
            mCallback?.onTerminate()
        }
        return super.dispatchTouchEvent(event)
    }

    fun setButtonTouchCallback(callback: RecordButtonTouchCallback?) {
        mCallback = callback
    }

    companion object {
        /**
         * 最短录音时间
         */
        private const val MIN_INTERVAL_TIME = 1000L
    }
}