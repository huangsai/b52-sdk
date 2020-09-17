package com.mobile.sdk.sister.socket

import com.mobile.guava.jvm.coroutines.Bus
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.file.AppPreferences
import com.mobile.sdk.sister.data.http.*
import com.mobile.sdk.sister.ui.asSimple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.encodeUtf8
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

    fun postLogin() = GlobalScope.launch(Dispatchers.IO) {
        try {
            val req = ReqLogin(
                AppPreferences.username,
                AppPreferences.token,
                2,
                1
            )
            val content = SisterX.component.json()
                .adapter(ReqLogin::class.java)
                .toJson(req)
                .encodeUtf8()

            CommonMessage.Builder()
                .bizId(IM_BUZ_LOGIN)
                .msgType(2)
                .content(content)
                .build()
                .also {
                    AppWebSocket.post(CommonMessage.ADAPTER.encodeByteString(it))
                }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun postLogout() = GlobalScope.launch(Dispatchers.IO) {
        try {
            CommonMessage.Builder()
                .bizId(IM_BUZ_LOGOUT)
                .msgType(2)
                .content("{}".encodeUtf8())
                .build()
                .also {
                    AppWebSocket.post(CommonMessage.ADAPTER.encodeByteString(it))
                }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun postMessage(dbMessage: DbMessage) = GlobalScope.launch(Dispatchers.IO) {
        var newDbMessage: DbMessage
        try {
            val content = SisterX.component.json()
                .adapter(ApiSimpleMessage::class.java)
                .toJson(dbMessage.toApiMessage().asSimple())
                .encodeUtf8()

            CommonMessage.Builder()
                .bizId(IM_BUZ_MSG)
                .msgType(2)
                .content(content)
                .build()
                .also {
                    AppWebSocket.post(CommonMessage.ADAPTER.encodeByteString(it))
                }

            newDbMessage = dbMessage
        } catch (e: Exception) {
            Timber.e(e)
            newDbMessage = dbMessage.copy(status = STATUS_MSG_FAILED)
        }
        if (SisterX.component.sisterRepository().insetMessage(newDbMessage) > 0) {
            if (newDbMessage.status == STATUS_MSG_FAILED) {
                Timber.tag("AppWebSocket").d("发送失败->${newDbMessage.id}")
                Bus.offer(SisterX.BUS_MSG_STATUS, dbMessage)
            }
        }
    }

    fun onMessage(commonMessage: CommonMessage) {
        val ack = SisterX.component.json()
            .adapter(ApiAck::class.java)
            .fromJson(commonMessage.content.string(Charsets.UTF_8))!!
            .also {
                Timber.tag("AppWebSocket").d("Ack->${it.msg}->${it.id}")
            }

        when (commonMessage.bizId) {
            IM_BUZ_LOGIN -> {
            }
            IM_BUZ_LOGOUT -> {
            }
            IM_BUZ_CLOSE -> {
            }
            IM_BUZ_NOTIFICATION -> {
            }
            IM_BUZ_MSG -> {
                require(!ack.id.isNullOrEmpty())
                markDbMessageSuccess(ack.id)
            }
            else -> Timber.tag("AppWebSocket").d("未知socket业务消息")
        }
    }

    private fun markDbMessageSuccess(id: String) = GlobalScope.launch(Dispatchers.IO) {
        val repo = SisterX.component.sisterRepository()
        repo.getMessageById(id)?.let { dbMessage ->
            if (repo.updateMessage(dbMessage.copy(status = STATUS_MSG_SUCCESS)) == 1) {
                Timber.tag("AppWebSocket").d("发送成功->$id")
                Bus.offer(SisterX.BUS_MSG_STATUS, dbMessage)
            }
        }
    }
}