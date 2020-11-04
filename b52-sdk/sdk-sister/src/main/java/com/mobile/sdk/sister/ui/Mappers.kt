package com.mobile.sdk.sister.ui

import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.file.AppPrefs
import com.mobile.sdk.sister.data.http.*
import com.mobile.sdk.sister.proto.ChatMsg
import com.mobile.sdk.sister.proto.ChatReq
import com.mobile.sdk.sister.proto.QueueTimeOutMsg
import com.mobile.sdk.sister.proto.ResponseResult
import com.squareup.moshi.Types
import java.util.*

const val MSG_TIME_DIFF = 10 * 60 * 1000L // 消息显示时间差

fun String.jsonToRobot(): List<ApiSysReply> {
    val type = Types.newParameterizedType(List::class.java, ApiSysReply::class.java)
    return SisterX.component.json()
        .adapter<List<ApiSysReply>>(type)
        .lenient()
        .fromJson(this)!!
}

fun List<ApiSysReply>.toJson(): String {
    val type = Types.newParameterizedType(List::class.java, ApiSysReply::class.java)
    return SisterX.component.json()
        .adapter<List<ApiSysReply>>(type)
        .lenient()
        .toJson(this)
}

fun String.jsonToText(): DbMessage.Text {
    return SisterX.component.json()
        .adapter(DbMessage.Text::class.java)
        .lenient()
        .fromJson(this)!!
}

fun String.jsonToImage(): DbMessage.Image {
    return SisterX.component.json()
        .adapter(DbMessage.Image::class.java)
        .lenient()
        .fromJson(this)!!
}

fun String.jsonToAudio(): DbMessage.Audio {
    return SisterX.component.json()
        .adapter(DbMessage.Audio::class.java)
        .lenient()
        .fromJson(this)!!
}

fun String.jsonToDeposit(): DbMessage.Deposit {
    return SisterX.component.json()
        .adapter(DbMessage.Deposit::class.java)
        .lenient()
        .fromJson(this)!!
}

fun String.jsonToTime(): DbMessage.Time {
    return SisterX.component.json()
        .adapter(DbMessage.Time::class.java)
        .lenient()
        .fromJson(this)!!
}

fun String.jsonToSystem(): DbMessage.System {
    return SisterX.component.json()
        .adapter(DbMessage.System::class.java)
        .lenient()
        .fromJson(this)!!
}

fun String.jsonToUpgrade(): DbMessage.Upgrade {
    return SisterX.component.json()
        .adapter(DbMessage.Upgrade::class.java)
        .lenient()
        .fromJson(this)!!
}

fun DbMessage.Text.toJson(): String {
    return SisterX.component.json()
        .adapter(DbMessage.Text::class.java)
        .lenient()
        .toJson(this)
}

fun DbMessage.Image.toJson(): String {
    return SisterX.component.json()
        .adapter(DbMessage.Image::class.java)
        .lenient()
        .toJson(this)
}

fun DbMessage.Audio.toJson(): String {
    return SisterX.component.json()
        .adapter(DbMessage.Audio::class.java)
        .lenient()
        .toJson(this)
}

fun DbMessage.Deposit.toJson(): String {
    return SisterX.component.json()
        .adapter(DbMessage.Deposit::class.java)
        .lenient()
        .toJson(this)
}

fun DbMessage.Time.toJson(): String {
    return SisterX.component.json()
        .adapter(DbMessage.Time::class.java)
        .lenient()
        .toJson(this)
}

fun DbMessage.System.toJson(): String {
    return SisterX.component.json()
        .adapter(DbMessage.System::class.java)
        .lenient()
        .toJson(this)
}

fun DbMessage.Upgrade.toJson(): String {
    return SisterX.component.json()
        .adapter(DbMessage.Upgrade::class.java)
        .lenient()
        .toJson(this)
}

fun DbMessage.toJson(): String {
    return SisterX.component.json()
        .adapter(DbMessage::class.java)
        .lenient()
        .toJson(this)
}

fun ChatMsg.toDbMessage(): DbMessage {
    return DbMessage(
        0L,
        id,
        msgType,
        AppPrefs.userId,
        sayContent,
        sayTime,
        fromImgUrl,
        fromUserName,
        fromUserId,
        1,
        chatId,
        STATUS_MSG_SUCCESS
    )
}

fun List<ApiSysReply>.toDbMessage(): DbMessage {
    return DbMessage(
        0L,
        UUID.randomUUID().toString(),
        TYPE_ROBOT,
        AppPrefs.userId,
        this.toJson(),
        System.currentTimeMillis(),
        "",
        "",
        "0",
        1,
        0,
        STATUS_MSG_SUCCESS
    )
}

fun String.toDbMessage(): DbMessage {
    return DbMessage(
        0L,
        UUID.randomUUID().toString(),
        TYPE_TEXT,
        AppPrefs.userId,
        DbMessage.Text(this).toJson(),
        System.currentTimeMillis(),
        "",
        "",
        "0",
        1,
        0,
        STATUS_MSG_SUCCESS
    )
}


fun DbMessage.toChatRes(): ChatReq {
    return ChatReq.Builder()
        .id(id)
        .msgType(type)
        .sayContent(content)
        .chatType(0)
        .toUserId(toUserId)
        .chatId(SisterX.chatId)
        .build()
}

fun DbMessage.crossTime(): DbMessage {
    return copy(
        type = TYPE_TIME,
        content = DbMessage.Time(time).toJson()
    )
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