package com.mobile.sdk.sister.session

import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.db.DbSession

class SessionFlow(private val session: DbSession) {
    private val messages = ArrayList<DbMessage>()
    private val repository = SisterX.component.sisterRepository()

    fun start() {
        if (messages.isNotEmpty()) {
            messages.clear()
        }
    }

    fun stop() {
    }

    private fun pull() {
    }

    private fun load() {


    }
}