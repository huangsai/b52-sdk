package com.mobile.sdk.sister.data

import com.mobile.guava.data.PlatformContext
import com.mobile.guava.data.toSource
import com.mobile.guava.jvm.Guava
import com.mobile.guava.jvm.domain.Source
import com.mobile.sdk.sister.data.db.AppDatabase
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.file.PlatformPrefs
import com.mobile.sdk.sister.data.http.*
import com.mobile.sdk.sister.ui.MSG_TIME_DIFF
import com.mobile.sdk.sister.ui.crossTime
import okhttp3.RequestBody
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SisterRepository @Inject constructor(
    private val dataService: DataService,
    private val platformContext: PlatformContext,
    private val appDatabase: AppDatabase,
    private val platformPrefs: PlatformPrefs
) {

    suspend fun user(username: String): Source<ApiUser> {
        return try {
            dataService.token(2, username).execute().toSource {
                platformPrefs.userId = it.userId
                platformPrefs.loginName = it.username
                platformPrefs.token = it.token
                platformPrefs.salt = it.salt
                platformPrefs.userImage = it.userImage
                platformPrefs.nickname = it.nickname.ifEmpty { it.username }
                return@toSource it
            }
        } catch (e: Exception) {
            if (username.isEmpty()) {
                platformPrefs.userId = ""
                platformPrefs.loginName = username
                platformPrefs.token = ""
                platformPrefs.salt = ""
                platformPrefs.userImage = ""
                platformPrefs.nickname = "游客"
                errorSource(e)
            } else {
                user("")
            }
        }
    }

    fun loadMessage(): List<DbMessage> {
        try {
            val list = appDatabase.messageDao()
                .getByUserId(platformPrefs.userId)
                .apply {
                    forEach {
                        if (it.status == STATUS_MSG_PROCESSING) {
                            it.status = STATUS_MSG_FAILED
                        }
                    }
                }
                .toMutableList()

            if (list.size > 2) {
                for (i in 1 until list.size) {
                    if (list[i].time - list[i - 1].time >= MSG_TIME_DIFF) {
                        list.add(i, list[i].crossTime())
                    }
                }
            }
            if (list.isNotEmpty()) {
                list.add(0, list[0].crossTime())
            }
            return list.toList()
        } catch (e: Exception) {
            Timber.d(e)
        }
        return emptyList()
    }

    fun updateMessage(dbMessage: DbMessage): Int = appDatabase.messageDao().update(dbMessage)

    fun insetMessage(dbMessage: DbMessage): Long = appDatabase.messageDao().insert(dbMessage)

    fun getMessageById(id: String): DbMessage? = appDatabase.messageDao().getById(id)

    fun messageCountById(id: String): Int = appDatabase.messageDao().countById(id)

    fun uploadFile(body: RequestBody): String {
        return try {
            dataService.uploadFile(body).execute()?.body()?.url ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun sysReply(): Source<List<ApiSysReply>> {
        return try {
            dataService.sysReply(1).execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun sysAutoReply(): Source<List<ApiSysReply>> {
        return try {
            dataService.sysReply(2).execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    private fun <T> errorSource(e: Throwable): Source<T> {
        Guava.timber.d(e)
        return Source.Error(e)
    }
}