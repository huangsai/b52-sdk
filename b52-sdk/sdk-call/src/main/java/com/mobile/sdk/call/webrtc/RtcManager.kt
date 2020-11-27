package com.mobile.sdk.call.webrtc

import android.content.Context
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.jvm.coroutines.Bus
import com.mobile.sdk.call.CallX.BUS_CALL_STATE
import com.mobile.sdk.call.CallX.TAG
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import org.webrtc.PeerConnection.*
import timber.log.Timber
import java.util.*

object RtcManager {

    private const val AUDIO_TRACK_ID = "ARDAMSa0"
    private val mPeerConnectionFactory: PeerConnectionFactory
    private val mRootEglBase: EglBase = EglBase.create()
    private lateinit var mAudioTrack: AudioTrack
    private var mPeerConnection: PeerConnection? = null

    init {
        mPeerConnectionFactory = createPeerConnectionFactory(AndroidX.myApp)
        initAudio()
    }

    private fun initAudio() {
        val audioSource = mPeerConnectionFactory.createAudioSource(MediaConstraints())
        mAudioTrack = mPeerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource)
        mAudioTrack.setEnabled(true)
    }

    private fun createPeerConnectionFactory(context: Context?): PeerConnectionFactory {
        val encoderFactory: VideoEncoderFactory
        val decoderFactory: VideoDecoderFactory
        encoderFactory = DefaultVideoEncoderFactory(
            mRootEglBase.eglBaseContext, false /* enableIntelVp8Encoder */, true
        )
        decoderFactory = DefaultVideoDecoderFactory(mRootEglBase.eglBaseContext)
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )
        val builder = PeerConnectionFactory.builder()
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
        builder.setOptions(null)
        return builder.createPeerConnectionFactory()
    }

    private fun createPeerConnection(): PeerConnection? {
        val configuration = RTCConfiguration(ArrayList())
        val connection: PeerConnection? =
            mPeerConnectionFactory.createPeerConnection(configuration, mPeerConnectionObserver)
        connection!!.addTrack(mAudioTrack)
        return connection
    }

    fun doStartCall() {
        if (mPeerConnection == null) {
            mPeerConnection = createPeerConnection()
        }
        val mediaConstraints = MediaConstraints()
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        mediaConstraints.optional.add(MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"))
        mPeerConnection!!.createOffer(object : SimpleSdpObserver() {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                mPeerConnection!!.setLocalDescription(SimpleSdpObserver(), sessionDescription)
                postMsg(sessionDescription)
            }
        }, mediaConstraints)

    }

    fun doEndCall() {
        hanup()
        postEndMsg()
    }

    private fun hanup() {
        mPeerConnection?.close()
        mPeerConnection = null
        updateCallState(true)
    }

    private fun doAnswerCall() {
        if (mPeerConnection == null) {
            mPeerConnection = createPeerConnection()
        }
        val sdpMediaConstraints = MediaConstraints()
        Timber.tag(TAG).i("Create answer ...")
        mPeerConnection!!.createAnswer(object : SimpleSdpObserver() {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                Timber.tag(TAG).i("Create answer success !")
                mPeerConnection!!.setLocalDescription(SimpleSdpObserver(), sessionDescription)
                postMsg(sessionDescription)
            }
        }, sdpMediaConstraints)
        updateCallState(false)
    }

    private fun updateCallState(state: Boolean) {
        Bus.offer(BUS_CALL_STATE, state)
    }

    private fun postEndMsg() {
    }

    private fun postMsg(sdp: SessionDescription) {
    }

    private val mPeerConnectionObserver: PeerConnection.Observer =
        object : PeerConnection.Observer {
            override fun onSignalingChange(signalingState: SignalingState) {
                Timber.tag(TAG).i("onSignalingChange: $signalingState")
            }

            override fun onIceConnectionChange(iceConnectionState: IceConnectionState) {
                Timber.tag(TAG).i("onIceConnectionChange: $iceConnectionState")
            }

            override fun onIceConnectionReceivingChange(b: Boolean) {
                Timber.tag(TAG).i("onIceConnectionChange: $b")
            }

            override fun onIceGatheringChange(iceGatheringState: IceGatheringState) {
                Timber.tag(TAG).i("onIceGatheringChange: $iceGatheringState")
            }

            override fun onIceCandidate(iceCandidate: IceCandidate) {
                Timber.tag(TAG).i("onIceCandidate: $iceCandidate")
                try {
//                    val message = JSONObject()
//                    message.put("userId", RTCSignalClient.getInstance().getUserId())
//                    message.put("msgType", RTCSignalClient.MESSAGE_TYPE_CANDIDATE)
//                    message.put("label", iceCandidate.sdpMLineIndex)
//                    message.put("id", iceCandidate.sdpMid)
//                    message.put("candidate", iceCandidate.sdp)
//                    RTCSignalClient.getInstance().sendMessage(message)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {
                for (i in iceCandidates.indices) {
                    Timber.tag(TAG).i("onIceCandidatesRemoved: %s", iceCandidates[i])
                }
                mPeerConnection?.removeIceCandidates(iceCandidates)
            }

            override fun onAddStream(mediaStream: MediaStream) {
                Timber.tag(TAG).i("onAddStream: %s", mediaStream.videoTracks.size)
            }

            override fun onRemoveStream(mediaStream: MediaStream) {
                Timber.tag(TAG).i("onRemoveStream")
            }

            override fun onDataChannel(dataChannel: DataChannel) {
                Timber.tag(TAG).i("onDataChannel")

            }

            override fun onRenegotiationNeeded() {
                Timber.tag(TAG).i("onRenegotiationNeeded")
            }

            override fun onAddTrack(rtpReceiver: RtpReceiver, mediaStreams: Array<MediaStream>) {
            }
        }

    open class SimpleSdpObserver : SdpObserver {
        override fun onCreateSuccess(sessionDescription: SessionDescription) {
            Timber.tag(TAG).i("SdpObserver: onCreateSuccess !")
        }

        override fun onSetSuccess() {
            Timber.tag(TAG).i("SdpObserver: onSetSuccess")
        }

        override fun onCreateFailure(msg: String) {
            Timber.tag(TAG).i("SdpObserver onCreateFailure: $msg")
        }

        override fun onSetFailure(msg: String) {
            Timber.tag(TAG).i("SdpObserver onSetFailure: $msg")
        }
    }

    fun onRemoteOfferReceived(userId: String, message: JSONObject) {
        Timber.tag(TAG).i("Receive Remote Call ...")
        if (mPeerConnection == null) {
            mPeerConnection = createPeerConnection()
        }
        try {
            val description = message.getString("sdp")
            mPeerConnection!!.setRemoteDescription(
                SimpleSdpObserver(),
                SessionDescription(SessionDescription.Type.OFFER, description)
            )
            doAnswerCall()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun onRemoteAnswerReceived(userId: String, message: JSONObject) {
        Timber.tag(TAG).i("Receive Remote Answer ...")
        try {
            val description = message.getString("sdp")
            mPeerConnection!!.setRemoteDescription(
                SimpleSdpObserver(),
                SessionDescription(SessionDescription.Type.ANSWER, description)
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        updateCallState(false)
    }


    private fun onRemoteCandidateReceived(userId: String, message: JSONObject) {
        Timber.tag(TAG).i("Receive Remote Candidate ...")
        try {
            val remoteIceCandidate = IceCandidate(
                message.getString("id"),
                message.getInt("label"),
                message.getString("candidate")
            )
            mPeerConnection!!.addIceCandidate(remoteIceCandidate)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun onRemoteHangup(userId: String) {
        Timber.tag(TAG).i("Receive Remote Hanup Event ...")
        hanup()
    }

}