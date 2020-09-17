package com.mobile.sdk.sister.socket

import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.sdk.sister.SisterX
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import timber.log.Timber

object AppWebSocket : LongLiveSocket() {

    private val webSocketListener: WebSocketListener = object : WebSocketListener() {

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            log("---------onClosed---------")
            AndroidX.isSocketConnected.postValue(false)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            log("---------onClosing---------")
            connectFailCount++
            AndroidX.isSocketConnected.postValue(false)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            log("---------onFailure---------")
            log(t)
            connectFailCount++
            AndroidX.isSocketConnected.postValue(false)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            log("---------onMessage---text------$text")
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            log("---------onMessage---bytes------%${bytes.size}")
            SocketUtils.onMessage(CommonMessage.ADAPTER.decode(SocketUtils.decrypt(bytes)))
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            log("---------onOpen---------")
            connectFailCount = 0
            AndroidX.isSocketConnected.postValue(true)
        }
    }

    private var internalSocket: WebSocket? = null
    private val socket: WebSocket get() = internalSocket!!

    init {
        AndroidX.isSocketConnected.observeForever {
            if (false == it) {
                reconnect(connectFailCount * 2000L)
            }
        }
    }

    override fun connect() {
        if (true != AndroidX.isNetworkConnected.value) return
        if (true == AndroidX.isSocketConnected.value) return

        suspendAction {
            val request: Request
            if (internalSocket != null) {
                request = socket.request()
                socket.close(1000, "reconnect")
            } else {
                request = Request.Builder()
                    .url("ws://127.0.0.1:30302/csms")
                    .build()
            }
            internalSocket = SisterX.component.okHttpClient().newWebSocket(request, webSocketListener)
            log("---------connect---------")
        }
    }

    override fun disconnect() {
        if (true != AndroidX.isNetworkConnected.value) return
        if (true != AndroidX.isSocketConnected.value) return
        suspendAction {
            if (internalSocket != null) {
                socket.close(1000, "disconnect")
                log("---------disconnect---------")
            }
        }
    }

    override fun post(bytes: ByteString) {
        if (internalSocket != null && true == AndroidX.isSocketConnected.value) {
            suspendAction {
                socket.send(SocketUtils.encrypt(bytes))
                log("---------post---bytes[${bytes.size}]")
            }
        }
    }

    override fun log(message: String) {
        Timber.tag("AppWebSocket").d(message)
    }

    override fun log(t: Throwable) {
        Timber.tag("AppWebSocket").d(t)
    }
}