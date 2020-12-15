package com.mobile.app.sdk

import androidx.multidex.MultiDexApplication
import com.mobile.sdk.sister.SisterX

class MyApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        SisterX.setup(this, BuildConfig.DEBUG)
        // SisterX.setServers("java.cg.xxx:30311")
        // SisterX.setServers("java.cg.xxx:31301")
        SisterX.setServers("java.cg.xxx:30311")
    }
}