package com.mobile.sdk.ipv6.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "code") val code: Int = -1,
    @Json(name = "msg") val msg: String? = null,
    @Json(name = "data") val data: T? = null
)