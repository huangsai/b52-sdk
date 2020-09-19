package com.mobile.sdk.sister.ui

import androidx.annotation.WorkerThread
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import com.mobile.guava.android.ensureWorkThread
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.base.InputStreamRequestBody
import com.mobile.sdk.sister.data.SisterRepository
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.file.AppPreferences
import com.mobile.sdk.sister.data.http.ApiNotice
import com.mobile.sdk.sister.data.http.STATUS_MSG_PROCESSING
import com.mobile.sdk.sister.socket.SocketUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        val uploadedUrl = sisterRepository.uploadFile(createRequestBody(image.url))
        if (uploadedUrl.isNotEmpty()) {
            dbMessage.content = DbMessage.Image(uploadedUrl).toJson()
            SocketUtils.postMessage(dbMessage)
        }
    }

    @WorkerThread
    fun postAudio(dbMessage: DbMessage) {
        ensureWorkThread()
        val audio = dbMessage.content.jsonToAudio()
        val uploadedUrl = sisterRepository.uploadFile(createRequestBody(audio.url))
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

    private fun createRequestBody(url: String): RequestBody {
        val uri = url.toUri()
        val doc = DocumentFile.fromSingleUri(AndroidX.myApp, uri)!!
        val contentPart = InputStreamRequestBody(
            (doc.type ?: "application/octet-stream").toMediaType(),
            AndroidX.myApp.contentResolver,
            uri
        )
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", doc.name, contentPart)
            .build()
    }
}