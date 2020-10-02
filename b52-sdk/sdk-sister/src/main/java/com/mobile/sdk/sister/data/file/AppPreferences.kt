package com.mobile.sdk.sister.data.file

import com.mobile.guava.android.log.uniqueId
import com.mobile.guava.android.mvvm.AndroidX
import com.tencent.mmkv.MMKV
import java.util.*

object AppPreferences : PlatformPreferences {

    private val prefs: MMKV by lazy {
        MMKV.initialize(AndroidX.myApp)
        MMKV.defaultMMKV()
    }

    override var username: String
        get() = prefs.decodeString("username", "")
        set(value) {
            prefs.encode("username", value)
        }

    override var userImage: String
        get() = prefs.decodeString("userImage", "")
        set(value) {
            prefs.encode("userImage", value)
        }

    override var nickname: String
        get() = prefs.decodeString("nickname", "")
        set(value) {
            prefs.encode("nickname", value)
        }

    override var userId: String
        get() = prefs.decodeString("userId", "")
        set(value) {
            prefs.encode("userId", value)
        }

    override var token: String
        get() = prefs.decodeString("token", "")
        set(value) {
            prefs.encode("token", value)
        }

    override var salt: String
        get() = prefs.decodeString("salt", "")
        set(value) {
            prefs.encode("salt", value)
        }

    override var deviceId: String
        get() {
            var value = prefs.decodeString("deviceId", "")
            if (value.isNullOrEmpty()) {
                value = try {
                    uniqueId(AndroidX.myApp)
                } catch (e: Exception) {
                    UUID.randomUUID().toString()
                }
            }
            prefs.encode("deviceId", value)
            return value
        }
        set(value) {
            prefs.encode("deviceId", value)
        }

    override var chatId: Long
        get() = prefs.decodeLong("chatId", 0L)
        set(value) {
            prefs.encode("chatId", value)
        }
}