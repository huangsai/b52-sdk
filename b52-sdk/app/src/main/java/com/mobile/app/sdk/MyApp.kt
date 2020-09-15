package com.mobile.app.sdk

import androidx.multidex.MultiDexApplication
import com.mobile.sdk.sister.BuildConfig
import com.mobile.sdk.sister.SisterLib

class MyApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        SisterLib.setup(this, BuildConfig.DEBUG)
    }
}