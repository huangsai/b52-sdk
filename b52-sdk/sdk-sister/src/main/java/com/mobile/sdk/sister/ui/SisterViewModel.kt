package com.mobile.sdk.sister.ui

import androidx.lifecycle.ViewModel
import com.mobile.sdk.sister.data.http.ApiMessage
import com.mobile.sdk.sister.data.http.SisterRepository
import com.mobile.sdk.sister.ui.items.MsgItem
import javax.inject.Inject

class SisterViewModel @Inject constructor(
    private val sisterRepository: SisterRepository
) : ViewModel() {


    private fun buildMsgItems(data: List<ApiMessage>): List<MsgItem> {
        return List(data.size) {
            MsgItem.create(data[it])
        }
    }
}