package com.mobile.sdk.sister.socket

import androidx.annotation.WorkerThread
import com.mobile.guava.android.ensureWorkThread
import com.mobile.guava.jvm.coroutines.Bus
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.file.AppPrefs
import com.mobile.sdk.sister.data.http.*
import com.mobile.sdk.sister.proto.*
import com.mobile.sdk.sister.ui.items.MsgItem
import com.mobile.sdk.sister.ui.jsonToText
import com.mobile.sdk.sister.ui.toChatRes
import com.mobile.sdk.sister.ui.toDbMessage
import com.mobile.sdk.sister.ui.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.toByteString
import timber.log.Timber
import java.util.*

object SocketUtils {

    fun encrypt(byteString: ByteString): ByteString {
        val aseKey = UUID.randomUUID().toString().substring(0, 16)
        val encryptedAES = AesUtils.encrypt(byteString.toByteArray(), aseKey)
        val encryptedRSA = RSAUtils.encryptByPublicKey(aseKey.toByteArray())
        return Buffer().writeLongLe(encryptedRSA.size.toLong())
            .write(encryptedRSA)
            .writeLongLe(encryptedAES.size.toLong())
            .write(encryptedAES).readByteString()
    }

    fun decrypt(byteString: ByteString): ByteString {
        val buffer = Buffer().write(byteString)
        val rsaLength = buffer.readLongLe()
        val encryptedRSA = buffer.readByteArray(rsaLength)
        val aesLength = buffer.readLongLe()
        val encryptedAES = buffer.readByteArray(aesLength)
        val aseKey = RSAUtils.decryptByPrivateKey(encryptedRSA).toString(Charsets.UTF_8)
        val decryptedAES = AesUtils.decrypt(encryptedAES, aseKey)
        return decryptedAES.toByteString()
    }

    @WorkerThread
    fun postLogin() {
        if (!SisterX.hasUser) {
            return
        }
        ensureWorkThread()
        resetChat()
        val req = LoginReq.Builder()
            .userName(AppPrefs.loginName)
            .token(AppPrefs.token)
            .salt(AppPrefs.salt)
            .deviceId(AppPrefs.deviceId)
            .chatType(0)
            .userType(0)
            .build()

        CommonMessage.Builder()
            .bizId(BUZ_LOGIN)
            .msgType(2)
            .content(req.encodeByteString())
            .build()
            .let {
                AppWebSocket.post(CommonMessage.ADAPTER.encodeByteString(it))
            }
    }

    fun leaveMessage(msg: String) = GlobalScope.launch(Dispatchers.IO) {
        CommonMessage.Builder()
            .bizId(BUZ_LEAVE_MSG)
            .msgType(2)
            .content(LeaveMsgReq.Builder().msg(msg).build().encodeByteString())
            .build()
            .let {
                AppWebSocket.post(CommonMessage.ADAPTER.encodeByteString(it))
            }
    }

    fun requestSister() = GlobalScope.launch(Dispatchers.IO) {
        CommonMessage.Builder()
            .bizId(BUZ_SISTER_REQUEST)
            .msgType(2)
            .content(MathCsReq.Builder().chatType(0).build().encodeByteString())
            .build()
            .let {
                AppWebSocket.post(CommonMessage.ADAPTER.encodeByteString(it))
            }
    }

    @WorkerThread
    fun postMessage(dbMessage: DbMessage) {
        ensureWorkThread()
        if (SisterX.hasSister()) {
            CommonMessage.Builder()
                .bizId(BUZ_MSG_REQUEST)
                .msgType(2)
                .content(dbMessage.toChatRes().encodeByteString())
                .build()
                .let {
                    if (true == SisterX.isSocketConnected.value) {
                        Timber.tag(SisterX.TAG).d("发送->%s", dbMessage.toJson())
                        AppWebSocket.post(CommonMessage.ADAPTER.encodeByteString(it))
                    } else {
                        dbMessage.status = STATUS_MSG_FAILED
                        SisterX.component.sisterRepository().updateMessage(dbMessage)

                        Bus.offer(SisterX.BUS_MSG_STATUS, dbMessage)
                    }
                }
        } else {
            setDbMessageSuccess(dbMessage.id)
            if (dbMessage.type == TYPE_TEXT) {
                sysReply(dbMessage.content.jsonToText().msg)
            } else {
                sysReply("")
            }
        }
    }

    fun onMessage(commonMessage: CommonMessage) {
        when (commonMessage.bizId) {
            BUZ_LOGIN -> {
                ResponseResult.ADAPTER.decode(commonMessage.content).let {
                    SisterX.isLogin.postValue(true)
                    Timber.tag(SisterX.TAG).d(it.msg)
                }

                requestSister()
            }
            BUZ_LOGOUT -> {
                SisterX.isLogin.postValue(false)
                resetChat()
                Timber.tag(SisterX.TAG).d("登出")
            }
            BUZ_CHAT_CLOSE_BY_MYSELF -> {
                resetChat()
                Timber.tag(SisterX.TAG).d("我关闭当前会话")
            }
            BUZ_CHAT_CLOSE_BY_SISTER -> {
                resetChat()
                Timber.tag(SisterX.TAG).d("客服关闭当前会话")
            }
            BUZ_CHAT_CLOSE_TIMEOUT -> {
                resetChat()
                Timber.tag(SisterX.TAG).d("会话超时")
            }
            BUZ_SISTER_REQUEST_TIMEOUT -> {
                QueueTimeOutMsg.ADAPTER.decode(commonMessage.content).let {
                    onRequestSisterError(it.timeOutMsg)
                    Timber.tag(SisterX.TAG).d(it.timeOutMsg.ifEmpty { "客服匹配超时" })
                }
            }
            BUZ_SISTER_REQUEST_ERROR -> {
                CSOfflineMsg.ADAPTER.decode(commonMessage.content).let {
                    onRequestSisterError(it.offlineMsg)
                    Timber.tag(SisterX.TAG).d(it.offlineMsg.ifEmpty { "无人在线请留言" })
                }
            }
            BUZ_SISTER_REQUEST_SUCCESS -> {
                MathCsMsg.ADAPTER.decode(commonMessage.content).let {
                    SisterX.chatId = it.chatId
                    SisterX.sisterUserId = it.toUserId.ifEmpty { "0" }
                    Timber.tag(SisterX.TAG).d("客服匹配成功")
                }
            }
            BUZ_MSG_NOTIFICATION -> {
                ChatMsg.ADAPTER.decode(commonMessage.content).let {
                    if (!it.id.isNullOrEmpty()) {
                        insertDbMessage(it.toDbMessage())
                        Timber.tag(SisterX.TAG).d("收到信息->%s", it.id)
                    }
                }
            }
            BUZ_MSG_REQUEST -> {
                ResponseResult.ADAPTER.decode(commonMessage.content).let {
                    if (!it.id.isNullOrEmpty()) {
                        setDbMessageSuccess(it.id)
                        Timber.tag(SisterX.TAG).d("发送成功->%s", it.id)
                    }
                }
            }
            else -> Timber.tag(SisterX.TAG).d("未知socket业务消息")
        }
    }

    private fun setDbMessageSuccess(id: String) = GlobalScope.launch(Dispatchers.IO) {
        val sisterRepository = SisterX.component.sisterRepository()
        sisterRepository.getMessageById(id)?.let { dbMessage ->
            dbMessage.status = STATUS_MSG_SUCCESS
            sisterRepository.updateMessage(dbMessage)

            Bus.offer(SisterX.BUS_MSG_STATUS, dbMessage)
        }
    }

    fun insertDbMessage(dbMessage: DbMessage) = GlobalScope.launch(Dispatchers.IO) {
        SisterX.component.sisterRepository().let {
            if (dbMessage.id.length > 1) {
                it.insetMessage(dbMessage)
            }
            Bus.offer(SisterX.BUS_MSG_NEW, MsgItem.create(dbMessage))
        }
    }

    private fun sysReply(keyword: String) = GlobalScope.launch(Dispatchers.IO) {
        try {
            val source = SisterX.component.sisterRepository().sysReply(2, keyword)
            when (source) {
                is Source.Success -> insertDbMessage(source.requireData().toDbMessage())
                is Source.Error -> Timber.tag(SisterX.TAG).d(source.requireError())
            }.exhaustive
        } catch (e: Exception) {
            Timber.tag(SisterX.TAG).d(e)
        }
    }

    private fun onRequestSisterError(msg: String) {
        resetChat()
        val obj = DbMessage(
            0L,
            "",
            TYPE_LEAVE_MSG,
            AppPrefs.userId,
            DbMessage.Text(msg).toJson(),
            System.currentTimeMillis(),
            "",
            "",
            "0",
            1,
            0,
            STATUS_MSG_SUCCESS
        )
        Bus.offer(SisterX.BUS_MSG_NEW, MsgItem.create(obj))
    }

    private fun resetChat() {
        SisterX.sisterUserId = "0"
        SisterX.chatId = 0L
    }
}