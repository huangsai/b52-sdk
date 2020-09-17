package com.mobile.sdk.sister.data

import com.mobile.guava.https.PlatformContext
import com.mobile.guava.https.toSource
import com.mobile.guava.jvm.Guava
import com.mobile.guava.jvm.domain.Source
import com.mobile.sdk.sister.data.db.AppDatabase
import com.mobile.sdk.sister.data.file.PlatformPreferences
import com.mobile.sdk.sister.data.http.ApiUser
import com.mobile.sdk.sister.data.http.DataService
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
        val call = dataService.token(2, platformPreferences.username)
        return try {
            call.execute().toSource {
                platformPreferences.userId = it.userId
                platformPreferences.username = it.username
                platformPreferences.token = it.token
                platformPreferences.userImage = it.userImage
                platformPreferences.nickname = if (it.nickname.isEmpty()) {
                    it.username
                } else {
                    it.nickname
                }
                return@toSource it
            }
        } catch (e: Exception) {
            platformPreferences.userId = 0L
            platformPreferences.username = ""
            platformPreferences.token = ""
            platformPreferences.userImage = ""
            platformPreferences.nickname = "游客"
            errorSource(e)
        }
    }

    private fun <T> errorSource(e: Throwable): Source<T> {
        Guava.timber.d(e)
        return Source.Error(e)
    }
}