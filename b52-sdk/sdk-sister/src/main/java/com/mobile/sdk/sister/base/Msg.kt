package com.mobile.sdk.sister.base

import android.widget.Toast
import androidx.annotation.StringRes
import com.mobile.guava.android.mvvm.AndroidX

object Msg {

    fun toast(msg: String) {
        Toast.makeText(AndroidX.myApp, msg, Toast.LENGTH_LONG).show()
    }

    fun toast(@StringRes msg: Int) {
        Toast.makeText(AndroidX.myApp, msg, Toast.LENGTH_LONG).show()
    }
}
