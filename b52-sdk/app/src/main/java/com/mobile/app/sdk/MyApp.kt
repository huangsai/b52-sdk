package com.mobile.app.sdk

import androidx.multidex.MultiDexApplication
import com.mobile.sdk.ipv6.Ipv6X
import com.mobile.sdk.sister.SisterX

class MyApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        SisterX.setup(this, BuildConfig.DEBUG)
        // Ipv6X.setup(this, BuildConfig.DEBUG)
        SisterX.setServers("java.cg.xxx", "java.cg.xxx")
    }
}