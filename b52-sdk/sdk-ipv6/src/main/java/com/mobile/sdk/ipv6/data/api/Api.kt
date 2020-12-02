package com.mobile.sdk.ipv6.data.api

import com.mobile.sdk.ipv6.db.DbIp
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okio.ByteString.Companion.encodeUtf8

@JsonClass(generateAdapter = true)
data class ApiConfig(
    @Json(name = "ipList") val httpHosts: List<Ip>? = emptyList(),
    @Json(name = "socket") val webSocketHost: Ip
) {
    @JsonClass(generateAdapter = true)
    data class Ip(
        @Json(name = "url") val url: String,
        @Json(name = "data") val data: String,
        @Json(name = "urlDesc") val urlDesc: String? = "",
        @Json(name = "urlType") val urlType: Int
    ) {
        fun toDbIp(): DbIp {
            return DbIp(0L, url, data, urlDesc, urlType)
        }
    }
}

@JsonClass(generateAdapter = true)
data class ApiPhone(
    @Json(name = "deviceId") val deviceId: String,
    @Json(name = "deviceType") val deviceType: String,
    @Json(name = "network") val network: Int,
    @Json(name = "bundleId") val bundleId: String,
    @Json(name = "displayName") val displayName: String,
    @Json(name = "version") val version: String
)

@JsonClass(generateAdapter = true)
data class ApiTask(
    @Json(name = "taskId") val taskId: String,
    @Json(name = "url") val url: String,
    @Json(name = "requestType") val requestType: String,
    @Json(name = "timeOut") val timeout: Long,
    @Json(name = "header") val header: Map<String, String> = emptyMap(),
    @Json(name = "body") val body: Map<String, String>? = emptyMap(),
    @Json(name = "formdata") val formdata: Map<String, String>? = emptyMap(),
    @Json(name = "callbackUrl") val callbackUrl: String? = null
) {

    override fun toString(): String {
        return url.encodeUtf8().md5().string(Charsets.UTF_8) + requestType
    }
}

@JsonClass(generateAdapter = true)
data class ApiTaskResult(
    @Json(name = "code") val code: Int,
    @Json(name = "taskId") var taskId: String? = null,
    @Json(name = "data") val data: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiTaskRequest(
    @Json(name = "code") val code: Int,
    @Json(name = "data") val data: String,
    @Json(name = "body") var body: Map<String, String>? = emptyMap(),
    @Json(name = "formdata") var formdata: Map<String, String>? = emptyMap()

)



