package com.mobile.sdk.sister.ui

import androidx.lifecycle.ViewModel
import com.mobile.sdk.sister.data.SisterRepository
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
                1,
                ApiMessage.Text("系统通知系统通知系统通知系统通知系统通知").toJson()
            ).asNormal()
        )

        list.add(
            ApiSimpleMessage(
                "",
                TYPE_TEXT,
                1,
                ApiMessage.Text("系统通知系通知系统通知系统通知").toJson()
            ).asNormal()
        )

        list.add(
            ApiSimpleMessage(
                "",
                TYPE_TEXT,
                1,
                ApiMessage.Text("系统通知系统通知").toJson()
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
                TYPE_IMAGE,
                1,
                ApiMessage.Image("爱的看哈扣税的啊的口号是").toJson()
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
                1,
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

        return list
    }
}