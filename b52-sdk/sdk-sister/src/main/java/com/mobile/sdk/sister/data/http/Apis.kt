package com.mobile.sdk.sister.data.http

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

const val STATUS_MSG_PROCESSING = 1
const val STATUS_MSG_SUCCESS = 2
const val STATUS_MSG_FAILED = 3

@JsonClass(generateAdapter = true)
data class ApiHelp(
    @Json(name = "type") val type: Int,
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class ApiNotice(
    @Json(name = "id") val id: Long,
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class ApiUser(
    @Json(name = "visitIp") val visitIp: String,
    @Json(name = "comeFrom") val comeFrom: Int,
    @Json(name = "id") val id: String,
    @Json(name = "username") val username: String,
    @Json(name = "level") val level: Int,
    @Json(name = "nickname") val nickname: String,
    @Json(name = "remark") val remark: String,
    @Json(name = "avatar") val userImage: String,
    @Json(name = "userIp") val userIp: String,
    @Json(name = "userId") val userId: Long,
    @Json(name = "userLevel") val userLevel: String,
    @Json(name = "salt") val salt: String,
    @Json(name = "token") val token: String
)