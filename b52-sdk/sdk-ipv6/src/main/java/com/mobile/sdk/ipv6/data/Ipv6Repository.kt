package com.mobile.sdk.ipv6.data

import androidx.annotation.WorkerThread
import com.mobile.guava.android.ensureWorkThread
import com.mobile.guava.jvm.domain.Source
import com.mobile.sdk.ipv6.data.api.ApiConfig
import com.mobile.sdk.ipv6.db.AppDatabase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Ipv6Repository @Inject constructor(
    private val dataService: DataService,
    private val appDatabase: AppDatabase
) {

    @WorkerThread
    suspend fun config(): Source<List<ApiConfig.Ip>> {
        ensureWorkThread()
        return try {
            val apiConfig = dataService.config()
            val list = ArrayList<ApiConfig.Ip>()
            list.add(apiConfig.webSocketHost)
            list.addAll(apiConfig.httpHosts.orEmpty())
            appDatabase.ipDao().also {
                it.clear()
                it.insertAll(list.map { ip ->  ip.toDbIp() })
            }
            Source.Success(list)
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    @WorkerThread
    fun loadIp(): List<ApiConfig.Ip> {
        ensureWorkThread()
        return appDatabase.ipDao().getAll().map { it.toIp() }
    }

    private fun <T> errorSource(e: Throwable): Source<T> {
        Timber.d(e)
        return Source.Error(e)
    }
}