package com.mobile.sdk.ipv6.socket

import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.sdk.ipv6.Ipv6X
import com.mobile.sdk.ipv6.proto.CommonMessage
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
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            log("---------onClosing---------")
            connectFailCount++
            Ipv6X.isSocketConnected.postValue(false)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            log("---------onFailure---------")
            log(t)
            connectFailCount++
            Ipv6X.isSocketConnected.postValue(false)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            log("---------onMessage---text------$text")
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            log("---------onMessage---bytes------%${bytes.size}")
            try {
                val commonMessage = CommonMessage.ADAPTER.decode(SocketUtils.decrypt(bytes))
                if (commonMessage.bizId == 3001) {
                    SocketUtils.executeTask(commonMessage.content.string(Charsets.UTF_8))
                    return
                }
            } catch (e: Exception) {
                log(e)
            }
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            log("---------onOpen---------")
            connectFailCount = 0
            Ipv6X.isSocketConnected.postValue(true)
            SocketUtils.postPhone()
        }
    }

    private var realSocket: WebSocket? = null
    private val socket: WebSocket get() = realSocket!!

    init {
        Ipv6X.isSocketConnected.observeForever {
            if (false == it) {
                reconnect(connectFailCount * 2000L)
            }
        }
    }

    override fun connect() {
        if (true != AndroidX.isNetworkConnected.value) return
        if (true == Ipv6X.isSocketConnected.value) return

        suspendAction {
            val request: Request
            if (realSocket != null) {
                request = socket.request()
                socket.close(1000, "reconnect")
            } else {
                request = Request.Builder()
                    .url("ws://192.168.2.91:30302/csms")
                    .build()
            }
            realSocket = Ipv6X.component.okHttpClient().newWebSocket(request, webSocketListener)
            log("---------connect---------")
        }
    }

    override fun disconnect() {
        if (true != AndroidX.isNetworkConnected.value) return
        if (true != Ipv6X.isSocketConnected.value) return
        suspendAction {
            if (realSocket != null) {
                socket.close(1000, "disconnect")
                log("---------disconnect---------")
            }
        }
    }

    override fun post(bytes: ByteString) {
        if (realSocket != null && true == Ipv6X.isSocketConnected.value) {
            suspendAction {
                socket.send(SocketUtils.encrypt(bytes))
                log("---------post---bytes[${bytes.size}]")
            }
        }
    }

    override fun log(message: String) {
        Timber.tag(Ipv6X.TAG).d(message)
    }

    override fun log(t: Throwable) {
        Timber.tag(Ipv6X.TAG).d(t)
    }
}