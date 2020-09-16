package com.mobile.sdk.sister.data

import com.mobile.guava.https.PlatformContext
import com.mobile.guava.https.toSource
import com.mobile.guava.jvm.Guava
import com.mobile.guava.jvm.domain.Source
import com.mobile.sdk.sister.data.db.AppDatabase
import com.mobile.sdk.sister.data.file.PlatformPreferences
import com.mobile.sdk.sister.data.http.ApiToken
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

    suspend fun token(): Source<ApiToken> {
        val call = dataService.token(2, platformPreferences.username, "192.168.2.208")
        return try {
            call.execute().toSource {
                platformPreferences.token = it.token
                return@toSource it
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