package com.mobile.sdk.sister.ui

import androidx.lifecycle.ViewModel
import com.mobile.guava.jvm.domain.Source
import com.mobile.sdk.sister.data.SisterRepository
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.http.ApiNotice
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SisterViewModel @Inject constructor(
    private val sisterRepository: SisterRepository
) : ViewModel() {

    fun loadMessages(): Flow<List<DbMessage>> {
        return sisterRepository.loadMessage()
    }

    suspend fun loadSystemNotices(): List<ApiNotice> {
        return listOf(
            ApiNotice(1L, "系统通知"),
            ApiNotice(1L, "系统通知系统通知系统通知系统通知"),
            ApiNotice(1L, "系统通知系统通知系统通知"),
            ApiNotice(1L, "系统通知系统通知系统通知系统通知系统通知系统通知"),
            ApiNotice(1L, "系统通知系统通知"),
        )
    }
}