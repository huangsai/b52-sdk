package com.mobile.sdk.sister.data.http

const val TYPE_TEXT = 1
const val TYPE_IMAGE = 2
const val TYPE_AUDIO = 3
const val TYPE_TIME = 4
const val TYPE_SYSTEM = 5
const val TYPE_DEPOSIT = 6
const val TYPE_UPGRADE = 7

data class ApiMessage(
    val id: Long,// 消息id
    val type: Int,// 消息类别，如文本、语音、图片、充值等
    val content: String,// 消息内容，客户端根据type类型去解析，它是一个json串
    val time: Long,// 发送时间（毫秒）
    val fromUserId: Long,// 发送方用户id
    val fromUserProfile: String,// 发送方用户头像
    val fromUsername: String,// 发送方用户名字
    val toUserId: Long,// 接收方用户id
    val toUsername: String// 接收方用户名
)

//快捷功能
data class ApiHelp(
    val type: Int,
    val content: String
)