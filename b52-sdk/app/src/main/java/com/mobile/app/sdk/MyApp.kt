package com.mobile.app.sdk

import androidx.multidex.MultiDexApplication
import com.mobile.sdk.sister.BuildConfig
import com.mobile.sdk.sister.SisterX

class MyApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        SisterX.setup(this, BuildConfig.DEBUG)
    }
}