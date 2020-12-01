package com.mobile.sdk.sister.data.file

import com.mobile.guava.android.mvvm.AppOAuth2Prefs

object AppPrefs : AppOAuth2Prefs(), PlatformPrefs {

    override var nickname: String
        get() = dataStore.decodeString("nickname", "")
        set(value) {
            dataStore.encode("nickname", value)
        }

    override var userImage: String
        get() = dataStore.decodeString("userImage", "")
        set(value) {
            dataStore.encode("userImage", value)
        }

    override var salt: String
        get() = dataStore.decodeString("salt", "")
        set(value) {
            dataStore.encode("salt", value)
        }
}