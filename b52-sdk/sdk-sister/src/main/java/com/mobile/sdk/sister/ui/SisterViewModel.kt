package com.mobile.sdk.sister.ui

import androidx.annotation.WorkerThread
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.mobile.guava.android.ensureWorkThread
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.data.SisterRepository
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.file.AppPreferences
import com.mobile.sdk.sister.data.http.ApiNotice
import com.mobile.sdk.sister.data.http.STATUS_MSG_PROCESSING
import com.mobile.sdk.sister.socket.SocketUtils
import java.io.File
import java.util.*
import javax.inject.Inject

class SisterViewModel @Inject constructor(
    private val sisterRepository: SisterRepository
) : ViewModel() {

    @WorkerThread
    fun loadMessages(): List<DbMessage> {
        ensureWorkThread()
        return sisterRepository.loadMessage()
    }

    @WorkerThread
    fun postText(dbMessage: DbMessage) {
        ensureWorkThread()
        return SocketUtils.postMessage(dbMessage)
    }

    @WorkerThread
    fun postImage(dbMessage: DbMessage) {
        ensureWorkThread()
        val image = dbMessage.content.jsonToImage()
        val uploadedUrl = sisterRepository.uploadImage(image.url.toUri())
        if (uploadedUrl.isNotEmpty()) {
            dbMessage.content = DbMessage.Image(uploadedUrl).toJson()
            SocketUtils.postMessage(dbMessage)
        }
    }

    @WorkerThread
    fun postAudio(dbMessage: DbMessage) {
        ensureWorkThread()
        val audio = dbMessage.content.jsonToAudio()
        val uploadedUrl = sisterRepository.uploadAudio(File(audio.url))
        if (uploadedUrl.isNotEmpty()) {
            dbMessage.content = DbMessage.Audio(audio.duration, uploadedUrl).toJson()
            SocketUtils.postMessage(dbMessage)
        }
    }

    @WorkerThread
    fun createDbMessage(type: Int, jsonContent: String): DbMessage {
        ensureWorkThread()
        return DbMessage(
            0,
            UUID.randomUUID().toString(),
            type,
            SisterX.toUserId,
            jsonContent,
            System.currentTimeMillis(),
            AppPreferences.username,
            AppPreferences.userImage,
            AppPreferences.userId,
            2,
            STATUS_MSG_PROCESSING
        )
    }

    fun loadSystemNotices(): List<ApiNotice> {
        return emptyList()
    }
}