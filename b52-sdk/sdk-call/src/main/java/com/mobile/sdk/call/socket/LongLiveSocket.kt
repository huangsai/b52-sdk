package com.mobile.sdk.call.socket

import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.jvm.coroutines.SingleRunner
import com.mobile.sdk.call.CallX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.ByteString

abstract class LongLiveSocket {

    private val singleRunner = SingleRunner()

    protected var connectFailCount = 0L
    protected var neededReconnect = true

    init {
        AndroidX.isNetworkConnected.observeForever {
            if (it) {
                connect()
            } else {
                CallX.isSocketConnected.value = false
            }
        }
    }

    protected fun suspendAction(action: suspend () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            singleRunner.afterPrevious {
                try {
                    action.invoke()
                } catch (e: Exception) {
                    log(e)
                }
            }
        }
    }

    protected fun reconnectWhenLose(delayTimeMillis: Long) {
        suspendAction {
            if (delayTimeMillis > 0) {
                delay(delayTimeMillis)
            }
            connect()
        }
    }

    protected abstract fun connect()

    protected abstract fun disconnect()

    protected abstract fun log(message: String)

    protected abstract fun log(t: Throwable)

    abstract fun post(bytes: ByteString)
}