package com.mobile.sdk.sister.socket

import androidx.annotation.WorkerThread
import com.mobile.guava.android.ensureWorkThread
import com.mobile.guava.jvm.coroutines.Bus
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.file.AppPrefs
import com.mobile.sdk.sister.data.http.*
import com.mobile.sdk.sister.proto.ChatMsg
import com.mobile.sdk.sister.proto.CommonMessage
import com.mobile.sdk.sister.proto.LoginReq
import com.mobile.sdk.sister.proto.ResponseResult
import com.mobile.sdk.sister.ui.items.MsgItem
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
        ensureWorkThread()
        val req = LoginReq.Builder()
            .userName(AppPrefs.username)
            .token(AppPrefs.token)
            .salt(AppPrefs.salt)
            .deviceId(AppPrefs.deviceId)
            .chatType(0)
            .userType(0)
            .build()

        CommonMessage.Builder()
            .bizId(IM_BUZ_LOGIN)
            .msgType(2)
            .content(req.encodeByteString())
            .build()
            .let {
                AppWebSocket.post(CommonMessage.ADAPTER.encodeByteString(it))
            }
    }

    @WorkerThread
    fun postMessage(dbMessage: DbMessage) {
        ensureWorkThread()
        val nextMessage = if (dbMessage.toUserId.isEmpty()) {
            dbMessage.copy(toUserId = "0")
        } else {
            dbMessage
        }
        CommonMessage.Builder()
            .bizId(IM_BUZ_MSG)
            .msgType(2)
            .content(nextMessage.toChatRes().encodeByteString())
            .build()
            .let {
                if (true == SisterX.isSocketConnected.value) {
                    Timber.tag(SisterX.TAG).d("发送->%s", nextMessage.toJson())
                    AppWebSocket.post(CommonMessage.ADAPTER.encodeByteString(it))
                } else {
                    dbMessage.status = STATUS_MSG_FAILED
                    SisterX.component.sisterRepository().updateMessage(dbMessage)

                    Bus.offer(SisterX.BUS_MSG_STATUS, dbMessage)
                }
            }
    }

    fun onMessage(commonMessage: CommonMessage) {
        when (commonMessage.bizId) {
            IM_BUZ_LOGIN -> ResponseResult.ADAPTER.decode(commonMessage.content).let {
                Timber.tag(SisterX.TAG).d(it.msg)
            }
            IM_BUZ_LOGOUT -> {
            }
            IM_BUZ_CLOSE_BY_MYSELF -> {
            }
            IM_BUZ_CLOSE_BY_SYSTEM -> {
            }
            IM_BUZ_CHAT_TIMEOUT -> {
            }
            IM_BUZ_NOTIFICATION -> ChatMsg.ADAPTER.decode(commonMessage.content).let {
                SisterX.toUserId = it.fromUserId
                insertDbMessage(it.toDbMessage())
            }
            IM_BUZ_MSG -> ResponseResult.ADAPTER.decode(commonMessage.content).let {
                if (!it.id.isNullOrEmpty()) {
                    setDbMessageSuccess(it.id)
                    Timber.tag(SisterX.TAG).d("发送成功->%s", it.id)
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

    private fun insertDbMessage(dbMessage: DbMessage) = GlobalScope.launch(Dispatchers.IO) {
        SisterX.component.sisterRepository().let {
            it.insetMessage(dbMessage)
            Bus.offer(SisterX.BUS_MSG_NEW, MsgItem.create(dbMessage))
        }
    }
}