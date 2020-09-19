package com.mobile.sdk.sister.data.http

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DataService {

    @GET("/member/getToken")
    fun token(
        @Query("comeFrom") from: Int,
        @Query("username") username: String
    ): Call<ApiUser>

    @POST("/oss/uploadFiles")
    fun uploadFile(@Body body: RequestBody): Call<ApiFile>
}