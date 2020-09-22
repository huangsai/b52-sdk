package com.mobile.sdk.sister.ui

import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.file.AppPreferences
import com.mobile.sdk.sister.data.http.STATUS_MSG_SUCCESS
import com.mobile.sdk.sister.socket.ChatMsg
import com.mobile.sdk.sister.socket.ChatReq

fun String.jsonToText(): DbMessage.Text {
    return SisterX.component.json()
        .adapter(DbMessage.Text::class.java)
        .fromJson(this)!!
}

fun String.jsonToImage(): DbMessage.Image {
    return SisterX.component.json()
        .adapter(DbMessage.Image::class.java)
        .fromJson(this)!!
}

fun String.jsonToAudio(): DbMessage.Audio {
    return SisterX.component.json()
        .adapter(DbMessage.Audio::class.java)
        .fromJson(this)!!
}

fun String.jsonToDeposit(): DbMessage.Deposit {
    return SisterX.component.json()
        .adapter(DbMessage.Deposit::class.java)
        .fromJson(this)!!
}

fun String.jsonToTime(): DbMessage.Time {
    return SisterX.component.json()
        .adapter(DbMessage.Time::class.java)
        .fromJson(this)!!
}

fun String.jsonToSystem(): DbMessage.System {
    return SisterX.component.json()
        .adapter(DbMessage.System::class.java)
        .fromJson(this)!!
}

fun String.jsonToUpgrade(): DbMessage.Upgrade {
    return SisterX.component.json()
        .adapter(DbMessage.Upgrade::class.java)
        .fromJson(this)!!
}

fun DbMessage.Text.toJson(): String {
    return SisterX.component.json()
        .adapter(DbMessage.Text::class.java)
        .toJson(this)
}

fun DbMessage.Image.toJson(): String {
    return SisterX.component.json()
        .adapter(DbMessage.Image::class.java)
        .toJson(this)
}

fun DbMessage.Audio.toJson(): String {
    return SisterX.component.json()
        .adapter(DbMessage.Audio::class.java)
        .toJson(this)
}

fun DbMessage.Deposit.toJson(): String {
    return SisterX.component.json()
        .adapter(DbMessage.Deposit::class.java)
        .toJson(this)
}

fun DbMessage.Time.toJson(): String {
    return SisterX.component.json()
        .adapter(DbMessage.Time::class.java)
        .toJson(this)
}

fun DbMessage.System.toJson(): String {
    return SisterX.component.json()
        .adapter(DbMessage.System::class.java)
        .toJson(this)
}

fun DbMessage.Upgrade.toJson(): String {
    return SisterX.component.json()
        .adapter(DbMessage.Upgrade::class.java)
        .toJson(this)
}

fun ChatMsg.toDbMessage(): DbMessage {
    return DbMessage(
        0L,
        id,
        msgType,
        AppPreferences.userId,
        sayContent,
        sayTime,
        fromImgUrl,
        fromUserName,
        fromUserId,
        fromUserType,
        STATUS_MSG_SUCCESS
    )
}

fun DbMessage.toChatRes(): ChatReq {
    return ChatReq.Builder()
        .id(id)
        .msgType(type)
        .sayContent(content)
        .chatType(1)
        .toUserId(toUserId.toString())
        .build()
}

fun Int.toAudioText(): String {
    val audioContent = StringBuilder()
    audioContent.append("$this''")
    val lengths = this / 6
    for (len: Int in 1..lengths) {
        if (len <= 10) {
            audioContent.append("\u3000")
        }
    }
    return audioContent.toString()
}

fun Int.toAudio2Text(): String {
    val audioContent = StringBuilder()
    val lengths = this / 6
    for (len: Int in 1..lengths) {
        if (len <= 10) {
            audioContent.append("\u3000")
        }
    }
    audioContent.append("$this''")
    return audioContent.toString()
}