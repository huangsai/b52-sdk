package com.mobile.app.sdk

import androidx.multidex.MultiDexApplication
import com.mobile.sdk.sister.SisterX

class MyApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        SisterX.setup(this, BuildConfig.DEBUG)
        SisterX.setServers(
            "ws://java.cg.xxx:30302/csms",
            "http://java.cg.xxx:30301/"
        )
    }
}