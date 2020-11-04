package com.mobile.sdk.sister.ui

import androidx.annotation.WorkerThread
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
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

    fun loadSystemNotices(): List<ApiNotice1> {
        return emptyList()
    }

    fun leaveMessage(msg: String) {
        SocketUtils.leaveMessage(msg)
    }

    fun requestSister() {
        SocketUtils.requestSister()
    }

    @WorkerThread
    suspend fun getSysReply(): Source<List<ApiSysReply>> {
        ensureWorkThread()
        return sisterRepository.sysReply()
    }

    @WorkerThread
    fun loadMessages(): List<DbMessage> {
        ensureWorkThread()
        return sisterRepository.loadMessage().also { list ->
            // 离线消息推送按照业务流程图，还是有问题的
            postOfflineMessages(list.filter {
                it.status == STATUS_MSG_PROCESSING || it.status == STATUS_MSG_FAILED
            })
        }
    }

    @WorkerThread
    fun postText(dbMessage: DbMessage) {
        ensureWorkThread()
        val isExist = sisterRepository.messageCountById(dbMessage.id) > 0
        if (isExist) {
            dbMessage.status = STATUS_MSG_PROCESSING
            Bus.offer(SisterX.BUS_MSG_STATUS, dbMessage)
        } else {
            sisterRepository.insetMessage(dbMessage)
        }
        SocketUtils.postMessage(dbMessage)
    }

    @WorkerThread
    fun postImage(dbMessage: DbMessage) {
        ensureWorkThread()
        val image = dbMessage.content.jsonToImage()
        val isExist = sisterRepository.messageCountById(dbMessage.id) > 0
        if (isExist) {
            dbMessage.status = STATUS_MSG_PROCESSING
            Bus.offer(SisterX.BUS_MSG_STATUS, dbMessage)
            if (image.url.startsWith("http", true)) {
                SocketUtils.postMessage(dbMessage)
                return
            }
        }

        require(STATUS_MSG_PROCESSING == dbMessage.status)
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

    private fun postOfflineMessages(list: List<DbMessage>) {
        list.forEach {
            when (it.type) {
                TYPE_TEXT -> postText(it)
                TYPE_IMAGE -> postImage(it)
                TYPE_AUDIO -> postAudio(it)
            }
        }
    }
}