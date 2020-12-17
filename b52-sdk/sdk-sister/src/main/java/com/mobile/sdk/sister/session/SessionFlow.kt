package com.mobile.sdk.sister.session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.db.DbSession
import com.mobile.sdk.sister.ui.items.MsgItem

class SessionFlow(
    private val session: DbSession
) {

    private val messages = ArrayList<DbMessage>()
    private val messagesItems = ArrayList<MsgItem>()
    private val repository = SisterX.component.sisterRepository()

    private val _oMessagesItems:MutableLiveData<List<MsgItem>> = MutableLiveData()
    val oMessagesItems: LiveData<List<MsgItem>> get() = _oMessagesItems

    fun start() {
        require(messages.isEmpty())
        require(messagesItems.isEmpty())
        messages.addAll(repository.loadMessage(session._id))
        messagesItems.addAll(messages.map { MsgItem.create(it) })
    }

    fun stop() {
    }

    fun requestSister() {
    }
}