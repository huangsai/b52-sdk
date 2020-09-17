package com.mobile.sdk.sister.ui

import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import com.mobile.guava.android.ensureWorkThread
import com.mobile.sdk.sister.data.SisterRepository
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.file.AppPreferences
import com.mobile.sdk.sister.data.http.*
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
    fun postText(content: String, toUserId: Long): DbMessage {
        ensureWorkThread()
        return DbMessage(
            0,
            UUID.randomUUID().toString(),
            TYPE_TEXT,
            toUserId,
            ApiMessage.Text(content).toJson(),
            System.currentTimeMillis(),
            AppPreferences.username,
            AppPreferences.userImage,
            AppPreferences.userId,
            2,
            STATUS_MSG_PROCESSING
        ).also {
            SocketUtils.postMessage(it)
        }
    }

    @WorkerThread
    fun postImage(file: File, toUserId: Long): DbMessage {
        ensureWorkThread()
        val uploadedUrl = sisterRepository.uploadImage(file)
        return DbMessage(
            0,
            UUID.randomUUID().toString(),
            TYPE_IMAGE,
            toUserId,
            ApiMessage.Image(uploadedUrl).toJson(),
            System.currentTimeMillis(),
            AppPreferences.username,
            AppPreferences.userImage,
            AppPreferences.userId,
            2,
            STATUS_MSG_PROCESSING
        ).also {
            SocketUtils.postMessage(it)
        }
    }

    @WorkerThread
    fun postAudio(file: File, duration: Long, toUserId: Long): DbMessage {
        ensureWorkThread()
        val uploadedUrl = sisterRepository.uploadAudio(file).ifEmpty {
            file.absolutePath
        }
        return DbMessage(
            0,
            UUID.randomUUID().toString(),
            TYPE_AUDIO,
            toUserId,
            ApiMessage.Audio(duration, uploadedUrl).toJson(),
            System.currentTimeMillis(),
            AppPreferences.username,
            AppPreferences.userImage,
            AppPreferences.userId,
            2,
            STATUS_MSG_PROCESSING
        ).also {
            SocketUtils.postMessage(it)
        }
    }

    fun loadSystemNotices(): List<ApiNotice> {
        return emptyList()
    }
}