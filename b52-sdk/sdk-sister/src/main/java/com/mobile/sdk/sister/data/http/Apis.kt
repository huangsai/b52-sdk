package com.mobile.sdk.sister.data.http

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

const val TYPE_TEXT = 1
const val TYPE_IMAGE = 2
const val TYPE_AUDIO = 3
const val TYPE_TIME = 4
const val TYPE_SYSTEM = 5
const val TYPE_DEPOSIT = 6
const val TYPE_UPGRADE = 7

@JsonClass(generateAdapter = true)
data class ApiSimpleMessage(
    @Json(name = "id") val id: String,
    @Json(name = "chatType") val type: Int,
    @Json(name = "sayContent") val content: String,
    @Json(name = "toUserId") val toUserId: Long
)

@JsonClass(generateAdapter = true)
data class ApiMessage(
    @Json(name = "id") val id: String,
    @Json(name = "sayContent") val content: String,
    @Json(name = "toUserId") val toUserId: Long,
    @Json(name = "chatType") val type: Int,
    @Json(name = "sayTime") val time: Long,
    @Json(name = "formImgUrl") val fromUserProfile: String,
    @Json(name = "formUserName") val formUsername: String,
    @Json(name = "fromUserId") val fromUserId: Long,
    @Json(name = "formUserType") val fromUserType: Int
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
        @Json(name = "url") val url: String,
        @Json(name = "type") val type: Int
    )
}

data class ApiHelp(
    val type: Int,
    val content: String
)