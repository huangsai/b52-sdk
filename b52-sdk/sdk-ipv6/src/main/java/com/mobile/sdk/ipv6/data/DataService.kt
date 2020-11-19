package com.mobile.sdk.ipv6.data

import com.mobile.sdk.ipv6.data.api.ApiConfig
import retrofit2.http.GET

interface DataService {

    @GET("/api/taskUrlConfig/getInitConfig")
    suspend fun config(): ApiConfig
}