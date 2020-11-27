package com.mobile.sdk.call.socket

import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.sdk.call.CallX
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject
import timber.log.Timber

object AppWebSocket : LongLiveSocket() {

    private val webSocketListener = object : WebSocketListener() {

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            log("---------onClosed---------")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            log("---------onClosing---------")
            connectFailCount++
            CallX.isSocketConnected.postValue(false)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            log("---------onFailure---------")
            log(t)
            connectFailCount++
            CallX.isSocketConnected.postValue(false)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            log("---------onMessage---text------$text")
            val jsonObject = JSONObject(text)
            val action = jsonObject.get("action")
            log("---------onMessage---action------$action")
            when (action) {
                "call" -> {
                }
            }

        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            log("---------onMessage---bytes------%${bytes.size}")
            try {
//                SocketUtils.onMessage(CommonMessage.ADAPTER.decode(SocketUtils.decrypt(bytes)))
            } catch (e: Exception) {
                log(e)
            }
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            log("---------onOpen---------")
            connectFailCount = 0
            CallX.isSocketConnected.postValue(true)
        }
    }

    private var realWebSocket: WebSocket? = null
    private val webSocket: WebSocket get() = realWebSocket!!

    init {
        CallX.isSocketConnected.observeForever {
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
        if (true == CallX.isSocketConnected.value) {
            return
        }

        suspendAction {
            val request: Request
            if (realWebSocket != null) {
                request = webSocket.request()
                webSocket.close(1000, "reconnect")
            } else {
                request = Request.Builder()
                    .url(CallX.socketServer)
                    .build()
            }
            realWebSocket = CallX.component.okHttpClient().newWebSocket(
                request, webSocketListener
            )
            log("---------connect---------")
        }
    }

    override fun disconnect() {
        if (true != AndroidX.isNetworkConnected.value) {
            return
        }
        if (true != CallX.isSocketConnected.value) {
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
        if (realWebSocket != null && true == CallX.isSocketConnected.value) {
            suspendAction {
                webSocket.send(SocketUtils.encrypt(bytes))
                log("---------post---bytes[${bytes.size}]")
            }
        }
    }

    override fun log(message: String) {
        Timber.tag(CallX.TAG).d(message)
    }

    override fun log(t: Throwable) {
        Timber.tag(CallX.TAG).d(t)
    }

    fun reconnect() {
        neededReconnect = true

        if (true != AndroidX.isNetworkConnected.value) {
            return
        }

        if (true == CallX.isSocketConnected.value) {
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