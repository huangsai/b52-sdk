package com.mobile.sdk.sister.socket

import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.proto.CommonMessage
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import timber.log.Timber

object AppWebSocket : LongLiveSocket() {

    private val webSocketListener = object : WebSocketListener() {

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            log("---------onClosed---------")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            log("---------onClosing---------")
            connectFailCount++
            SisterX.isSocketConnected.postValue(false)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            log("---------onFailure---------")
            log(t)
            connectFailCount++
            SisterX.isSocketConnected.postValue(false)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            log("---------onMessage---text------$text")
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            log("---------onMessage---bytes------%${bytes.size}")
            try {
                SocketUtils.onMessage(CommonMessage.ADAPTER.decode(SocketUtils.decrypt(bytes)))
            } catch (e: Exception) {
                log(e)
            }
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            log("---------onOpen---------")
            connectFailCount = 0
            SisterX.isSocketConnected.postValue(true)
        }
    }

    private var realWebSocket: WebSocket? = null
    private val webSocket: WebSocket get() = realWebSocket!!

    init {
        SisterX.isSocketConnected.observeForever {
            if (false == it) {
                if (neededReconnect) {
                    reconnectWhenLose(connectFailCount * 2000L)
                }
            }
        }
    }

    override fun connect() {
        if (true != AndroidX.isNetworkConnected.value) {
            return
        }
        if (true == SisterX.isSocketConnected.value) {
            return
        }

        suspendAction {
            val request: Request
            if (realWebSocket != null) {
                request = webSocket.request()
                webSocket.close(1000, "reconnect")
            } else {
                request = Request.Builder()
                    .url(SisterX.socketServer)
                    .build()
            }
            realWebSocket = SisterX.component.okHttpClient().newWebSocket(
                request, webSocketListener
            )
            log("---------connect---------")
        }
    }

    override fun disconnect() {
        if (true != AndroidX.isNetworkConnected.value) {
            return
        }
        if (true != SisterX.isSocketConnected.value) {
            return
        }
        suspendAction {
            if (realWebSocket != null) {
                neededReconnect = false
                webSocket.close(1000, "disconnect")
                log("---------disconnect---------")
            }
        }
    }

    override fun post(bytes: ByteString) {
        if (realWebSocket != null && true == SisterX.isSocketConnected.value) {
            suspendAction {
                webSocket.send(SocketUtils.encrypt(bytes))
                log("---------post---bytes[${bytes.size}]")
            }
        }
    }

    override fun log(message: String) {
        Timber.tag(SisterX.TAG).d(message)
    }

    override fun log(t: Throwable) {
        Timber.tag(SisterX.TAG).d(t)
    }

    fun reconnect() {
        neededReconnect = true

        if (true != AndroidX.isNetworkConnected.value) {
            return
        }

        if (true == SisterX.isSocketConnected.value) {
            suspendAction {
                if (realWebSocket != null) {
                    webSocket.close(1000, "disconnect")
                    log("---------disconnect---------")
                }
            }
        } else {
            connect()
        }
    }
}