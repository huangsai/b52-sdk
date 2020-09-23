package com.mobile.sdk.ipv6.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiRequest<out T : Any>(
    @Json(name = "token") val token: String,
    @Json(name = "data") val data: T? = null
)