package com.mobile.sdk.sister.data.http

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DataService {

    @GET("/member/getToken")
    fun token(
        @Query("comeForm") from: Int,
        @Query("username") username: String,
        @Query("visitIp") ipv6: String
    ): Call<ApiToken>
}