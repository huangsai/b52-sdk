package com.mobile.sdk.sister.socket

import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.jvm.coroutines.SingleRunner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.ByteString

abstract class LongLiveSocket {

    private val singleRunner = SingleRunner()

    protected var connectFailCount = 0L

    init {
        AndroidX.isNetworkConnected.observeForever {
            if (it) connect() else AndroidX.isSocketConnected.value = false
        }
    }

    protected fun reconnect(delayTimeMillis: Long) {
        if (true != AndroidX.isNetworkConnected.value) return
        suspendAction {
            if (delayTimeMillis > 0) {
                delay(delayTimeMillis)
            }
            connect()
        }
    }

    protected fun suspendAction(action: suspend () -> Unit) = GlobalScope.launch(Dispatchers.IO) {
        singleRunner.afterPrevious {
            try {
                action.invoke()
            } catch (e: Exception) {
                log(e)
            }
        }
    }

    protected abstract fun connect()

    protected abstract fun disconnect()

    protected abstract fun log(message: String)

    protected abstract fun log(t: Throwable)

    abstract fun post(bytes: ByteString)
}