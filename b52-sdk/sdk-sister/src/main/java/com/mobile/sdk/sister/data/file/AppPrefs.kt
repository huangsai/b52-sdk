package com.mobile.sdk.sister.data.file

import com.mobile.guava.android.log.uniqueId
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.android.mvvm.AppOAuth2Prefs
import com.tencent.mmkv.MMKV
import java.util.*

object AppPrefs : PlatformPrefs {

    override var userImage: String
        get() = AppOAuth2Prefs.requireDataStore().decodeString("userImage", "")
        set(value) {
            AppOAuth2Prefs.requireDataStore().encode("userImage", value)
        }

    override var nickname: String
        get() = AppOAuth2Prefs.requireDataStore().decodeString("nickname", "")
        set(value) {
            AppOAuth2Prefs.requireDataStore().encode("nickname", value)
        }


    override var salt: String
        get() = AppOAuth2Prefs.requireDataStore().decodeString("salt", "")
        set(value) {
            AppOAuth2Prefs.requireDataStore().encode("salt", value)
        }

    override val deviceId: String
        get() = AppOAuth2Prefs.deviceId

    override var flavorId: Int
        get() = AppOAuth2Prefs.flavorId
        set(value) {
            AppOAuth2Prefs.flavorId = value
        }

    override var loginName: String
        get() = AppOAuth2Prefs.loginName
        set(value) {
            AppOAuth2Prefs.loginName = value
        }

    override var loginPassword: String
        get() = AppOAuth2Prefs.loginPassword
        set(value) {
            AppOAuth2Prefs.loginPassword = value
        }

    override var token: String
        get() = AppOAuth2Prefs.token
        set(value) {
            AppOAuth2Prefs.token = value
        }

    override var userId: String
        get() = AppOAuth2Prefs.userId
        set(value) {
            AppOAuth2Prefs.userId = value
        }
}