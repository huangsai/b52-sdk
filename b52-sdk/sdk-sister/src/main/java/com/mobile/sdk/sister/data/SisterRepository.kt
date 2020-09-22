package com.mobile.sdk.sister.data

import com.mobile.guava.https.PlatformContext
import com.mobile.guava.https.toSource
import com.mobile.guava.jvm.Guava
import com.mobile.guava.jvm.domain.Source
import com.mobile.sdk.sister.data.db.AppDatabase
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.file.PlatformPreferences
import com.mobile.sdk.sister.data.http.ApiUser
import com.mobile.sdk.sister.data.http.DataService
import okhttp3.RequestBody
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SisterRepository @Inject constructor(
    private val dataService: DataService,
    private val platformContext: PlatformContext,
    private val appDatabase: AppDatabase,
    private val platformPreferences: PlatformPreferences
) {

    suspend fun user(username: String): Source<ApiUser> {
        return try {
            dataService.token(2, username).execute().toSource {
                platformPreferences.userId = it.userId
                platformPreferences.username = it.username
                platformPreferences.token = it.token
                platformPreferences.salt = it.salt
                platformPreferences.userImage = it.userImage
                platformPreferences.nickname = if (it.nickname.isEmpty()) {
                    it.username
                } else {
                    it.nickname
                }
                return@toSource it
            }
        } catch (e: Exception) {
            platformPreferences.userId = ""
            platformPreferences.username = username
            platformPreferences.token = ""
            platformPreferences.salt = ""
            platformPreferences.userImage = ""
            platformPreferences.nickname = "游客"
            errorSource(e)
        }
    }

    fun loadMessage(): List<DbMessage> {
        return try {
            appDatabase.messageDao().getByUserId(platformPreferences.userId)
        } catch (e: Exception) {
            Timber.d(e)
            emptyList()
        }
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

    private fun <T> errorSource(e: Throwable): Source<T> {
        Guava.timber.d(e)
        return Source.Error(e)
    }
}