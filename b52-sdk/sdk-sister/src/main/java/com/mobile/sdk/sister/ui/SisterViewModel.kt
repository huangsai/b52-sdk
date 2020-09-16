package com.mobile.sdk.sister.ui

import androidx.lifecycle.ViewModel
import com.mobile.sdk.sister.data.http.*
import javax.inject.Inject

class SisterViewModel @Inject constructor(
    private val sisterRepository: SisterRepository
) : ViewModel() {

    suspend fun loadMessages(): List<ApiMessage> {
        val list = ArrayList<ApiMessage>()
        list.add(
            ApiSimpleMessage(
                "",
                TYPE_TIME,
                1,
                ApiMessage.Time(System.currentTimeMillis()).toJson()
            ).asNormal()
        )
        list.add(
            ApiSimpleMessage(
                "",
                TYPE_TEXT,
                0,
                ApiMessage.Text("发送消息").toJson()
            ).asNormal()
        )

        list.add(
            ApiSimpleMessage(
                "",
                TYPE_TEXT,
                1,
                ApiMessage.Text("收到消息").toJson()
            ).asNormal()
        )

        list.add(
            ApiSimpleMessage(
                "",
                TYPE_TEXT,
                1,
                ApiMessage.Text("收到消息收到消息收到消息收到消息").toJson()
            ).asNormal()
        )

        list.add(
            ApiSimpleMessage(
                "",
                TYPE_TEXT,
                0,
                ApiMessage.Text("发送消息发送消息发送消息发送消息发送消息").toJson()
            ).asNormal()
        )
        list.add(
            ApiSimpleMessage(
                "",
                TYPE_IMAGE,
                0,
                ApiMessage.Image("").toJson()
            ).asNormal()
        )
        list.add(
            ApiSimpleMessage(
                "",
                TYPE_IMAGE,
                1,
                ApiMessage.Image("").toJson()
            ).asNormal()
        )

        list.add(
            ApiSimpleMessage(
                "",
                TYPE_TIME,
                1,
                ApiMessage.Time(System.currentTimeMillis()).toJson()
            ).asNormal()
        )

        list.add(
            ApiSimpleMessage(
                "",
                TYPE_TEXT,
                0,
                ApiMessage.Text("充值方式").toJson()
            ).asNormal()
        )

        list.add(
            ApiSimpleMessage(
                "",
                TYPE_DEPOSIT,
                1,
                ApiMessage.Deposit("支付宝", 1, "").toJson()
            ).asNormal()
        )


        list.add(
            ApiSimpleMessage(
                "",
                TYPE_AUDIO,
                1,
                ApiMessage.Audio(5000L, "").toJson()
            ).asNormal()
        )

        list.add(
            ApiSimpleMessage(
                "",
                TYPE_AUDIO,
                0,
                ApiMessage.Audio(5000L, "").toJson()
            ).asNormal()
        )

        return list
    }

    suspend fun loadSystemNotices(): List<ApiMessage> {
        val list = ArrayList<ApiMessage>()
        list.add(
            ApiSimpleMessage(
                "",
                TYPE_SYSTEM,
                1,
                ApiMessage.System("系统通知").toJson()
            ).asNormal()
        )

        list.add(
            ApiSimpleMessage(
                "",
                TYPE_SYSTEM,
                1,
                ApiMessage.System("系统通知系统通知系统通知系统通知系统通知").toJson()
            ).asNormal()
        )

        list.add(
            ApiSimpleMessage(
                "",
                TYPE_SYSTEM,
                1,
                ApiMessage.System("系统通知系统通知系统通知").toJson()
            ).asNormal()
        )

        list.add(
            ApiSimpleMessage(
                "",
                TYPE_SYSTEM,
                1,
                ApiMessage.System("系统通知系统通知").toJson()
            ).asNormal()
        )

        return list
    }
}