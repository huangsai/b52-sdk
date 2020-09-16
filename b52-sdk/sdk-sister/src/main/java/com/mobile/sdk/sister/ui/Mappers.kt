package com.mobile.sdk.sister.ui

import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.data.file.AppPreferences
import com.mobile.sdk.sister.data.http.ApiMessage
import com.mobile.sdk.sister.data.http.ApiSimpleMessage

fun String.jsonToText(): ApiMessage.Text {
    return SisterX.component.json()
        .adapter(ApiMessage.Text::class.java)
        .fromJson(this)!!
}

fun String.jsonToImage(): ApiMessage.Image {
    return SisterX.component.json()
        .adapter(ApiMessage.Image::class.java)
        .fromJson(this)!!
}

fun String.jsonToAudio(): ApiMessage.Audio {
    return SisterX.component.json()
        .adapter(ApiMessage.Audio::class.java)
        .fromJson(this)!!
}

fun String.jsonToDeposit(): ApiMessage.Deposit {
    return SisterX.component.json()
        .adapter(ApiMessage.Deposit::class.java)
        .fromJson(this)!!
}

fun String.jsonToTime(): ApiMessage.Time {
    return SisterX.component.json()
        .adapter(ApiMessage.Time::class.java)
        .fromJson(this)!!
}

fun String.jsonToSystem(): ApiMessage.System {
    return SisterX.component.json()
        .adapter(ApiMessage.System::class.java)
        .fromJson(this)!!
}

fun String.jsonToUpgrade(): ApiMessage.Upgrade {
    return SisterX.component.json()
        .adapter(ApiMessage.Upgrade::class.java)
        .fromJson(this)!!
}

fun ApiMessage.Text.toJson(): String {
    return SisterX.component.json()
        .adapter(ApiMessage.Text::class.java)
        .toJson(this)
}

fun ApiMessage.Image.toJson(): String {
    return SisterX.component.json()
        .adapter(ApiMessage.Image::class.java)
        .toJson(this)
}

fun ApiMessage.Audio.toJson(): String {
    return SisterX.component.json()
        .adapter(ApiMessage.Audio::class.java)
        .toJson(this)
}

fun ApiMessage.Deposit.toJson(): String {
    return SisterX.component.json()
        .adapter(ApiMessage.Deposit::class.java)
        .toJson(this)
}

fun ApiMessage.Time.toJson(): String {
    return SisterX.component.json()
        .adapter(ApiMessage.Time::class.java)
        .toJson(this)
}

fun ApiMessage.System.toJson(): String {
    return SisterX.component.json()
        .adapter(ApiMessage.System::class.java)
        .toJson(this)
}

fun ApiMessage.Upgrade.toJson(): String {
    return SisterX.component.json()
        .adapter(ApiMessage.Upgrade::class.java)
        .toJson(this)
}

fun ApiMessage.asSimple(): ApiSimpleMessage {
    return ApiSimpleMessage(id, type, toUserId, content)
}

fun ApiSimpleMessage.asNormal(): ApiMessage {
    return ApiMessage(
        id,
        type,
        toUserId,
        content,
        System.currentTimeMillis(),
        AppPreferences.userImage,
        AppPreferences.username,
        AppPreferences.userId,
        2
    )
}