package com.mobile.sdk.call.data.http

import com.mobile.sdk.call.CallX
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class HostSelectionInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (isHttpApi(request.url.toString())) {
            val dynamicHttpUrl = createHttpUrl(request.url.toString())
            val newHttpUrl = request.url.newBuilder()
                .host(dynamicHttpUrl.host)
                .scheme(dynamicHttpUrl.scheme)
                .port(dynamicHttpUrl.port)
                .build()
            return chain.proceed(request.newBuilder().url(newHttpUrl).build())
        }
        return chain.proceed(request)
    }

    private fun createHttpUrl(original: String): HttpUrl {
        return CallX.httpServer.toHttpUrl()
    }

    private fun isHttpApi(original: String): Boolean {
        return when {
            original.contains("member/getToken") -> true
            original.contains("oss/uploadFiles") -> true
            original.contains("sysReply/listSysReply") -> true
            else -> false
        }
    }
}