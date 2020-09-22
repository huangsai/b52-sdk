package com.mobile.sdk.sister.ui.chat

import android.graphics.drawable.AnimationDrawable
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
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
    private val pressAnimDrawable: AnimationDrawable
    private var disposable: Disposable? = null
    private var mMediaRecorder = MediaRecorder()
    private var duration: Long = 0
    private var audioFile: File? = null

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
        pressAnimDrawable = balloon.getContentView()
            .findViewById<ImageView>(R.id.press_audio_anim).drawable as AnimationDrawable
    }

    private fun showPopup() {
        balloon.showAlignTop(binding.layoutInput, 0, -50)
    }

    private fun startRecord() {
        pressAnimDrawable.start()
        countDuration()
        try {
            audioFile = File(
                fragment.requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                System.currentTimeMillis().toString() + ".aac"
            )
            //创建录音文件
            audioFile?.createNewFile()
            //从麦克风采集
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            //最终的保存文件为aac格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            //所有android系统都支持的适中采样的频率
            mMediaRecorder.setAudioSamplingRate(44100)
            //通用的AAC编码格式
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            //设置音质频率
            mMediaRecorder.setAudioEncodingBitRate(1024 * 1024)
            //设置文件录音的位置
            mMediaRecorder.setOutputFile(audioFile?.absolutePath)
            //开始录音
            mMediaRecorder.prepare()
            mMediaRecorder.start()
        } catch (e: Exception) {
        }
    }

    private fun stopRecord() {
        pressAnimDrawable.stop()
        mMediaRecorder.reset()
    }

    private fun sendMsg() {
        if (audioFile != null)
            fragment.chatListPresenter.postAudio(duration * 1000, audioFile!!)
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
     * 这里判断是否可以正常录音，false可以正常录音
     */
    override fun isIntercept(): Boolean {
        return !micAvailability()
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

    /**
     *  检查麦克风是否被占用
     *  返回true就是没有被占用。
     *  返回false就是被占用。
     */
    private fun micAvailability(): Boolean {
        var available = true
        val recorder: AudioRecord? = AudioRecord(
            MediaRecorder.AudioSource.MIC, 44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_DEFAULT, 44100
        )
        try {
            if (recorder!!.recordingState != AudioRecord.RECORDSTATE_STOPPED) {
                available = false
            }
            recorder.startRecording()
            if (recorder.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
                recorder.stop()
                available = false
            }
            recorder.stop()
        } finally {
            recorder!!.release()
        }
        return available
    }
}