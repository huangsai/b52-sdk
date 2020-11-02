package com.mobile.sdk.sister.data.http

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

const val TYPE_TEXT = 1
const val TYPE_IMAGE = 2
const val TYPE_AUDIO = 3
const val TYPE_TIME = 4
const val TYPE_SYSTEM = 5
const val TYPE_DEPOSIT = 6
const val TYPE_AUTO_REPLY = 7
const val TYPE_LEAVE_MSG = 8

const val IM_BUZ_LOGIN = 20001// 登录20001
const val IM_BUZ_LOGOUT = 20002// 登出20002
const val IM_BUZ_MSG = 30001// 发送聊天消息30001
const val IM_BUZ_NOTIFICATION = 30002// 消息通知30002
const val IM_BUZ_CLOSE_BY_MYSELF = 30003// 关闭当前聊天30003
const val IM_BUZ_CLOSE_BY_SYSTEM = 30004// 关闭会话通知30004
const val IM_BUZ_CHAT_TIMEOUT = 30005// 会话超时通知30005
const val IM_BUZ_REQUEST_SISTER_ERROR = 30006// 排队超时通知30006
const val IM_BUZ_REQUEST_SISTER = 30007// 新增匹配客服30007
const val IM_BUZ_REQUEST_SISTER_SUCCESS = 30008// 匹配客服结果30008
const val IM_BUZ_LEAVE_MSG = 30009// 新增留言30009

const val STATUS_MSG_PROCESSING = 1
const val STATUS_MSG_SUCCESS = 2
const val STATUS_MSG_FAILED = 3

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
    @Json(name = "userId") val userId: String,
    @Json(name = "userLevel") val userLevel: String,
    @Json(name = "salt") val salt: String,
    @Json(name = "token") val token: String
)

@JsonClass(generateAdapter = true)
data class ApiFile(@Json(name = "url") val url: String)

@JsonClass(generateAdapter = true)
data class ApiSysReply(
    @Json(name = "id") val id: Long,
    @Json(name = "delFlag") val delFlag: Int,
    @Json(name = "flag") val flag: Int,
    @Json(name = "words") val words: String,
    @Json(name = "content") val content: String,
    @Json(name = "createBy") val createBy: String,
    @Json(name = "updateBy") val updateBy: String,
    @Json(name = "createTime") val createTime: String,
    @Json(name = "updateTime") val updateTime: String,
    @Json(name = "version") val version: Int
) {
    @JsonClass(generateAdapter = true)
    data class Req(
        @Json(name = "flag") val flag: Int
    )
}

@JsonClass(generateAdapter = true)
data class ApiNotice1(
    @Json(name = "id") val id: Long,
    @Json(name = "content") val content: String
)