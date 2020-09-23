package com.mobile.sdk.ipv6.data

import com.mobile.sdk.ipv6.Ipv6X
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException

class HostSelectionInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        try {
            return chain.proceed(request)
        } catch (e: SocketTimeoutException) {
            if (e is IOException && isHttpApi(request.url.toString())) {
                for (i: Int in Ipv6X.httpHosts.indices) {
                    val newHttpUrl = Ipv6X.httpHosts[i].url.toHttpUrl()
                    val finalHttpUrl = request.url.newBuilder()
                        .host(newHttpUrl.host)
                        .scheme(newHttpUrl.scheme)
                        .port(newHttpUrl.port)
                        .build()

                    try {
                        return chain.proceed(request.newBuilder().url(finalHttpUrl).build())
                    } catch (ex: Exception) {
                        if (ex is IOException) {
                            continue
                        } else {
                            throw ex
                        }
                    }
                }
            }
            throw e
        }
    }

    private fun isHttpApi(url: String): Boolean {
        return when {
            url.contains("taskUrlConfig/getInitConfig") -> true
            else -> false
        }
    }
}