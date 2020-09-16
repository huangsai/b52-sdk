package com.mobile.sdk.sister.ui.chat

import android.widget.TextView
import com.mobile.guava.android.mvvm.Msg
import com.mobile.guava.android.mvvm.lifecycle.SimplePresenter
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.views.RecordButtonTouchCallback
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.createBalloon
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

class ChatVoicePresenter(
    private val chatFragment: ChatFragment,
    private val binding: SisterFragmentChatBinding
) : SimplePresenter(), RecordButtonTouchCallback {

    private val balloon: Balloon
    private val pressDuration: TextView
    private val pressStatus: TextView
    private var disposable: Disposable? = null

    init {
        binding.pressVoice.setButtonTouchCallback(this)
        balloon = createBalloon(chatFragment.requireContext()) {
            setLayout(R.layout.sister_popup_press_voice)
            cornerRadius = 0f
            arrowVisible = false
            setBackgroundDrawableResource(R.drawable.sister_press_voice_popup_bg)
        }
        pressDuration = balloon.getContentView().findViewById(R.id.press_duration)
        pressStatus = balloon.getContentView().findViewById(R.id.press_status)
    }

    private fun showPopup() {
        balloon.showAlignTop(binding.layoutInput, 0, -50)
    }

    private fun startRecord() {
        Msg.toast("开始录制")
        countDuration()
    }

    private fun stopRecord() {
        Msg.toast("结束录制")
        clearDisposable()
    }

    private fun sendMsg() {
        Msg.toast("发送语音消息")
    }

    /**
     * 计算录制时长
     */
    private fun countDuration() {
        clearDisposable()
        disposable = Flowable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { it + 1 }
            .subscribe {
                pressDuration.text = "${it}''"
            }
    }

    private fun clearDisposable() {
        pressDuration.text = null
        if (disposable != null && !disposable?.isDisposed!!) disposable?.dispose()
    }

    /**
     * 这里判断录音权限，如果没有，则返回false，并请求获取权限
     */
    override fun isIntercept(): Boolean {
        return false
    }

    /**
     * 开始录制
     */
    override fun onStartAudio() {
        showPopup()
        startRecord()
    }

    /**
     * 取消录制
     */
    override fun onCancel() {
        stopRecord()
    }

    /**
     * 停止录制
     */
    override fun onFinish() {
        stopRecord()
        sendMsg()
    }

    /**
     * 松手，隐藏弹窗
     */
    override fun onTerminate() {
        balloon.dismiss()
    }

    /**
     * 录制时间太短
     */
    override fun onTouchIntervalTimeSmall() {
        Msg.toast(chatFragment.getString(R.string.sister_popup_voice_duration_less))
    }

    /**
     * 滑动到了取消区域
     */
    override fun onTouchCancelArea() {
        pressStatus.text = chatFragment.getString(R.string.sister_popup_voice_press_cancel_text)
    }

    /**
     * 滑动到正常区域
     */
    override fun onRestoreNormalTouchArea() {
        pressStatus.text = chatFragment.getString(R.string.sister_popup_voice_press_send_text)
    }
}