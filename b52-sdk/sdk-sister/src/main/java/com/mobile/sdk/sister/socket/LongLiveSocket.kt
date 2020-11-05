package com.mobile.sdk.sister.socket

import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.jvm.coroutines.SingleRunner
import com.mobile.sdk.sister.SisterX
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
            if (true == it) connect() else SisterX.isSocketConnected.value = false
        }
    }

    protected fun reconnect(delayTimeMillis: Long) {
        if (true != AndroidX.isNetworkConnected.value) return
        if (!SisterX.hasUser) return

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