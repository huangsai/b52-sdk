package com.mobile.sdk.sister.data

import com.mobile.guava.data.PlatformContext
import com.mobile.guava.data.toSource
import com.mobile.guava.jvm.Guava
import com.mobile.guava.jvm.domain.Source
import com.mobile.sdk.call.data.http.ApiSysReply
import com.mobile.sdk.call.data.http.ApiUser
import com.mobile.sdk.call.data.http.DataService
import com.mobile.sdk.sister.data.db.AppDatabase
import com.mobile.sdk.sister.data.db.DbMessage
import com.mobile.sdk.sister.data.file.PlatformPrefs
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallRepository @Inject constructor(
    private val dataService: DataService,
    private val platformContext: PlatformContext,
    private val appDatabase: AppDatabase,
    private val platformPrefs: PlatformPrefs
) {

    suspend fun user(_loginName: String): Source<ApiUser> {
        return try {
            dataService.token(2, _loginName).execute().toSource {
                platformPrefs.userId = it.userId
                platformPrefs.loginName = _loginName
                platformPrefs.token = it.token
                platformPrefs.salt = it.salt
                platformPrefs.userImage = it.userImage
                platformPrefs.nickname = it.nickname.ifEmpty { it.username }
                return@toSource it
            }
        } catch (e: Exception) {
            if (_loginName.isEmpty()) {
                platformPrefs.userId = ""
                platformPrefs.loginName = _loginName
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

    suspend fun sysReply(flag: Int, keyword: String): Source<List<ApiSysReply>> {
        return try {
            dataService.sysReply(flag, keyword).execute().toSource {
                return@toSource if (2 == flag && it.size > 5) it.subList(0, 5) else it
            }
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    private fun <T> errorSource(e: Throwable): Source<T> {
        Guava.timber.d(e)
        return Source.Error(e)
    }
}