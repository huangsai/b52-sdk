package com.mobile.sdk.sister.data.file

import com.mobile.guava.data.OAuth2Prefs

interface PlatformPrefs : OAuth2Prefs {

    var nickname: String

    var userImage: String

    var salt: String
}