package com.mobile.sdk.sister.ui

import androidx.annotation.WorkerThread
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.guava.android.ensureWorkThread
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.jvm.coroutines.Bus
import com.mobile.guava.jvm.domain.Source
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.base.InputStreamRequestBody
import com.mobile.sdk.sister.data.SisterRepository
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.file.AppPrefs
import com.mobile.sdk.sister.data.http.*
import com.mobile.sdk.sister.socket.SocketUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*
import javax.inject.Inject

class SisterViewModel @Inject constructor(
    private val sisterRepository: SisterRepository
) : ViewModel() {

    /**
     * 获取充值聊天记录
     */
    fun loadChargeChatList(): List<ApiCharge> {
        return listOf(
            ApiCharge(1), ApiCharge(2), ApiCharge(3),
            ApiCharge(4), ApiCharge(5), ApiCharge(6)
        )
    }

    fun loadSystemNotices(): List<ApiNotice1> {
        return emptyList()
    }

    /**
     * 发送留言消息
     */
    fun leaveMessage(msg: String) {
        SocketUtils.leaveMessage(msg)
    }

    /**
     * 请求联系客服
     */
    fun requestSister() {
        if (!SisterX.hasSister()) {
            SocketUtils.requestSister()
        }
    }

    /**
     * 获取客服系统回复
     */
    @WorkerThread
    suspend fun getSysReply(): Source<List<ApiSysReply>> {
        ensureWorkThread()
        return sisterRepository.sysReply(1, "")
    }

    /**
     * 获取客服聊天记录
     */
    @WorkerThread
    fun loadMessages(): List<DbMessage> {
        ensureWorkThread()
        return sisterRepository.loadMessage()
    }

    /**
     * 发送普通文本消息
     */
    @WorkerThread
    fun postText(dbMessage: DbMessage) {
        ensureWorkThread()
        val isExist = sisterRepository.messageCountById(dbMessage.id) > 0
        if (isExist) {
            dbMessage.status = STATUS_MSG_PROCESSING
            //通知消息状态
            Bus.offer(SisterX.BUS_MSG_STATUS, dbMessage)
        } else {
            sisterRepository.insetMessage(dbMessage)
        }
        SocketUtils.postMessage(dbMessage)
    }

    /**
     * 发送图片消息
     */
    @WorkerThread
    fun postImage(dbMessage: DbMessage) {
        ensureWorkThread()
        val image = dbMessage.content.jsonToImage()
        val isExist = sisterRepository.messageCountById(dbMessage.id) > 0
        if (isExist) {
            dbMessage.status = STATUS_MSG_PROCESSING
            //通知消息状态
            Bus.offer(SisterX.BUS_MSG_STATUS, dbMessage)
            if (image.url.startsWith("http", true)) {
                SocketUtils.postMessage(dbMessage)
                return
            }
        }

        require(STATUS_MSG_PROCESSING == dbMessage.status)
        //上传文件
        val uploadedUrl = sisterRepository.uploadFile(createImageRequestBody(image.url))
        if (uploadedUrl.isNotEmpty()) {
            dbMessage.content = DbMessage.Image(uploadedUrl).toJson()
        } else {
            dbMessage.status = STATUS_MSG_FAILED
        }
        if (isExist) {
            sisterRepository.updateMessage(dbMessage)
        } else {
            sisterRepository.insetMessage(dbMessage)
        }
        when (dbMessage.status) {
            STATUS_MSG_PROCESSING -> SocketUtils.postMessage(dbMessage)
            STATUS_MSG_FAILED -> Bus.offer(SisterX.BUS_MSG_STATUS, dbMessage)
            else -> throw IllegalStateException()
        }
    }

    /**
     * 发送语音消息
     */
    @WorkerThread
    fun postAudio(dbMessage: DbMessage) {
        ensureWorkThread()
        val audio = dbMessage.content.jsonToAudio()
        val isExist = sisterRepository.messageCountById(dbMessage.id) > 0
        if (isExist) {
            dbMessage.status = STATUS_MSG_PROCESSING
            Bus.offer(SisterX.BUS_MSG_STATUS, dbMessage)
            if (audio.url.startsWith("http", true)) {
                SocketUtils.postMessage(dbMessage)
                return
            }
        }

        require(STATUS_MSG_PROCESSING == dbMessage.status)
        val uploadedUrl = sisterRepository.uploadFile(
            createAudioRequestBody(File(audio.url))
        )
        if (uploadedUrl.isNotEmpty()) {
            dbMessage.content = DbMessage.Audio(audio.duration, uploadedUrl).toJson()
        } else {
            dbMessage.status = STATUS_MSG_FAILED
        }
        if (isExist) {
            sisterRepository.updateMessage(dbMessage)
        } else {
            sisterRepository.insetMessage(dbMessage)
        }
        when (dbMessage.status) {
            STATUS_MSG_PROCESSING -> SocketUtils.postMessage(dbMessage)
            STATUS_MSG_FAILED -> Bus.offer(SisterX.BUS_MSG_STATUS, dbMessage)
            else -> throw IllegalStateException()
        }
    }

    @WorkerThread
    fun createDbMessage(type: Int, jsonContent: String): DbMessage {
        ensureWorkThread()
        return DbMessage(
            0,
            UUID.randomUUID().toString(),
            type,
            SisterX.sisterUserId,
            jsonContent,
            System.currentTimeMillis(),
            AppPrefs.userImage,
            AppPrefs.loginName,
            AppPrefs.userId,
            0,
            SisterX.chatId,
            STATUS_MSG_PROCESSING,
        )
    }

    /**
     * 发送系统回复消息
     */
    fun postSysReply(isHelp: Boolean, apiSysReply: ApiSysReply) {
        viewModelScope.launch(Dispatchers.IO) {
            createDbMessage(
                TYPE_TEXT,
                DbMessage.Text(if (isHelp) apiSysReply.words else apiSysReply.question!!).toJson()
            ).also {
                SocketUtils.insertDbMessage(it.copy(status = STATUS_MSG_SUCCESS))
                SocketUtils.insertDbMessage(apiSysReply.content.sisterTextDbMessage())
            }
        }
    }

    private fun createImageRequestBody(url: String): RequestBody {
        val uri = url.toUri()
        val doc = DocumentFile.fromSingleUri(AndroidX.myApp, uri)!!
        val contentPart = InputStreamRequestBody(
            "image/*".toMediaType(),
            AndroidX.myApp.contentResolver,
            uri
        )
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", doc.name, contentPart)
            .build()
    }

    private fun createAudioRequestBody(file: File): RequestBody {
        val contentPart = file.asRequestBody("audio/*".toMediaType())
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, contentPart)
            .build()
    }
}