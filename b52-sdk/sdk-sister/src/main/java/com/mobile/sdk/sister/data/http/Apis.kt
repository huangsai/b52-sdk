package com.mobile.sdk.sister.data.http

import com.mobile.sdk.sister.data.db.DbMessage
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

const val TYPE_TEXT = 1
const val TYPE_IMAGE = 2
const val TYPE_AUDIO = 3
const val TYPE_TIME = 4
const val TYPE_SYSTEM = 5
const val TYPE_DEPOSIT = 6

const val IM_BUZ_MSG = 30001
const val IM_BUZ_NOTIFICATION = 30002
const val IM_BUZ_CLOSE = 30002
const val IM_BUZ_LOGIN = 20001
const val IM_BUZ_LOGOUT = 20002

@JsonClass(generateAdapter = true)
data class ApiAck(
    @Json(name = "id") val id: String = "",
    @Json(name = "msg") val msg: String = "",
    @Json(name = "result") val status: Int = 1
) {
    fun success(): Boolean = status == 1
}

@JsonClass(generateAdapter = true)
data class ApiSimpleMessage(
    @Json(name = "id") val id: String,
    @Json(name = "chatType") val type: Int,
    @Json(name = "toUserId") val toUserId: Long,
    @Json(name = "sayContent") val content: String
)

@JsonClass(generateAdapter = true)
data class ApiMessage(
    @Json(name = "id") val id: String,
    @Json(name = "chatType") val type: Int,
    @Json(name = "toUserId") val toUserId: Long,
    @Json(name = "sayContent") val content: String,
    @Json(name = "sayTime") val time: Long,
    @Json(name = "fromImgUrl") val fromUserImage: String,
    @Json(name = "fromUserName") val fromUsername: String,
    @Json(name = "fromUserId") val fromUserId: Long,
    @Json(name = "fromUserType") val fromUserType: Int
) {

    @JsonClass(generateAdapter = true)
    data class Text(@Json(name = "msg") val msg: String)

    @JsonClass(generateAdapter = true)
    data class Audio(
        @Json(name = "duration") val duration: Long,
        @Json(name = "url") val url: String
    )

    @JsonClass(generateAdapter = true)
    data class Image(@Json(name = "url") val url: String)

    @JsonClass(generateAdapter = true)
    data class System(@Json(name = "msg") val msg: String)

    @JsonClass(generateAdapter = true)
    data class Upgrade(@Json(name = "msg") val msg: String)

    @JsonClass(generateAdapter = true)
    data class Time(@Json(name = "nano") val nano: Long)

    @JsonClass(generateAdapter = true)
    data class Deposit(
        @Json(name = "name") val name: String,
        @Json(name = "type") val type: Int,
        @Json(name = "url") val url: String
    )

    fun toDbMessage(): DbMessage {
        return DbMessage(
            id,
            type,
            toUserId,
            content,
            time,
            fromUserImage,
            fromUsername,
            fromUserId,
            fromUserType,
            0
        )
    }
}

@JsonClass(generateAdapter = true)
data class ApiHelp(
    @Json(name = "type") val type: Int,
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class ApiToken(
    @Json(name = "token") val token: String,
    @Json(name = "salt") val salt: String
)

@JsonClass(generateAdapter = true)
data class ReqLogin(
    @Json(name = "userName") val username: String,
    @Json(name = "token") val token: String,
    @Json(name = "userType") val userType: Int,
    @Json(name = "chatType") val chatType: Int
)