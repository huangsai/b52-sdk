package com.mobile.sdk.sister.ui.chat

import android.media.MediaRecorder
import android.os.Environment.DIRECTORY_MUSIC
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mobile.guava.android.mvvm.Msg
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.SisterViewModel
import com.mobile.sdk.sister.ui.views.RecordButtonTouchCallback
import com.pacific.adapter.AdapterViewHolder
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.createBalloon
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import java.io.File
import java.util.concurrent.TimeUnit


class ChatVoicePresenter(
    fragment: ChatFragment,
    binding: SisterFragmentChatBinding,
    model: SisterViewModel
) : BaseChatPresenter(fragment, binding, model), RecordButtonTouchCallback {

    private val balloon: Balloon
    private val pressDuration: TextView
    private val pressStatus: TextView
    private var disposable: Disposable? = null
    private var mMediaRecorder = MediaRecorder()
    private var duration: Long = 0
    private val audioFile = File(
        fragment.requireContext().getExternalFilesDir(DIRECTORY_MUSIC),
        System.currentTimeMillis().toString() + ".arm"
    )

    init {
        binding.pressVoice.setButtonTouchCallback(this)
        balloon = createBalloon(fragment.requireContext()) {
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
        countDuration()
        try {
            //创建录音文件
            audioFile.createNewFile()
            //从麦克风采集
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            //最终的保存文件为arm格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            //所有android系统都支持的适中采样的频率
            mMediaRecorder.setAudioSamplingRate(44100)
            //通用的ARM编码格式
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            //设置音质频率
            mMediaRecorder.setAudioEncodingBitRate(1024 * 1024)
            //设置文件录音的位置
            mMediaRecorder.setOutputFile(audioFile.absolutePath)
            //开始录音
            mMediaRecorder.prepare()
            mMediaRecorder.start()
        } catch (e: Exception) {
        }
    }

    private fun stopRecord() {
        mMediaRecorder.stop()
        mMediaRecorder.release()
    }

    private fun sendMsg() {
        fragment.chatListPresenter.postAudio(duration * 1000, audioFile)
    }

    /**
     * 计算录制时长
     */
    private fun countDuration() {
        clearDisposable()
        disposable = Flowable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { duration = it + 1 }
            .subscribe {
                pressDuration.text = "${duration}''"
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
        clearDisposable()
        balloon.dismissWithDelay(50)
    }

    /**
     * 录制时间太短
     */
    override fun onTouchIntervalTimeSmall() {
        Msg.toast(fragment.getString(R.string.sister_popup_voice_duration_less))
    }

    /**
     * 滑动到了取消区域
     */
    override fun onTouchCancelArea() {
        pressStatus.text = fragment.getString(R.string.sister_popup_voice_press_cancel_text)
    }

    /**
     * 滑动到正常区域
     */
    override fun onRestoreNormalTouchArea() {
        pressStatus.text = fragment.getString(R.string.sister_popup_voice_press_send_text)
    }

    override fun load() {
        TODO("Not yet implemented")
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        TODO("Not yet implemented")
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaRecorder.release()
    }
}