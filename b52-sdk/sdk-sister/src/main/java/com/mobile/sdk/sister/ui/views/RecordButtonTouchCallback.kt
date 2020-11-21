package com.mobile.sdk.sister.ui.views

interface RecordButtonTouchCallback {
    /**
     * 是否拦截，如果返回true，代表拦截，则不进行触摸事件的监听
     *
     * @return true为拦截，false为不拦截
     */
    fun isIntercept(): Boolean

    /**
     * 开始录制
     */
    fun onStartAudio()

    /**
     * 结束录制
     */
    fun onFinish()

    /**
     * 准备退出，在手指抬起时回调
     */
    fun onTerminate()

    /**
     * 间隔时间太小
     */
    fun onTouchIntervalTimeSmall()

    /**
     * 当触碰到取消区域
     */
    fun onTouchCancelArea()

    /**
     * 当恢复到正常区域
     */
    fun onRestoreNormalTouchArea()
}