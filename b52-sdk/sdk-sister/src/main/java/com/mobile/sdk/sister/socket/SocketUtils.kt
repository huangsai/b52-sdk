package com.mobile.sdk.sister.socket

import androidx.annotation.WorkerThread
import com.mobile.guava.android.ensureWorkThread
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.jvm.coroutines.Bus
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.file.AppPreferences
import com.mobile.sdk.sister.data.http.*
import com.mobile.sdk.sister.ui.items.MsgItem
import com.mobile.sdk.sister.ui.toChatRes
import com.mobile.sdk.sister.ui.toDbMessage
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
            .userName(AppPreferences.username)
            .token(AppPreferences.token)
            .salt(AppPreferences.salt)
            .chatType(1)
            .userType(2)
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
        CommonMessage.Builder()
            .bizId(IM_BUZ_MSG)
            .msgType(2)
            .content(dbMessage.toChatRes().encodeByteString())
            .build()
            .let {
                if (true == AndroidX.isSocketConnected.value) {
                    AppWebSocket.post(CommonMessage.ADAPTER.encodeByteString(it))
                } else {
                    dbMessage.status = STATUS_MSG_FAILED
                    SisterX.component.sisterRepository().updateMessage(dbMessage)

                    Bus.offer(SisterX.BUS_MSG_CHANGED, dbMessage)
                }
            }
    }

    fun onMessage(commonMessage: CommonMessage) {
        when (commonMessage.bizId) {
            IM_BUZ_LOGIN -> ResponseResult.ADAPTER.decode(commonMessage.content).let {
                Timber.tag("AppWebSocket").d(it.msg)
            }
            IM_BUZ_LOGOUT -> {
            }
            IM_BUZ_CLOSE_BY_MYSELF -> {
            }
            IM_BUZ_CLOSE_BY_SYSTEM -> {
            }
            IM_BUZ_NOTIFICATION -> ChatMsg.ADAPTER.decode(commonMessage.content).let {
                insertDbMessage(it.toDbMessage())
            }
            IM_BUZ_MSG -> ResponseResult.ADAPTER.decode(commonMessage.content).let {
                setDbMessageSuccess(it.id)
            }
            else -> Timber.tag("AppWebSocket").d("未知socket业务消息")
        }
    }

    private fun setDbMessageSuccess(id: String) = GlobalScope.launch(Dispatchers.IO) {
        val sisterRepository = SisterX.component.sisterRepository()
        sisterRepository.getMessageById(id)?.let { dbMessage ->
            dbMessage.status = STATUS_MSG_SUCCESS
            sisterRepository.updateMessage(dbMessage)

            Bus.offer(SisterX.BUS_MSG_CHANGED, dbMessage)
        }
    }

    private fun insertDbMessage(dbMessage: DbMessage) = GlobalScope.launch(Dispatchers.IO) {
        SisterX.component.sisterRepository().let {
            it.insetMessage(dbMessage)
            Bus.offer(SisterX.BUS_MSG_NEW, MsgItem.create(dbMessage))
        }
    }
}