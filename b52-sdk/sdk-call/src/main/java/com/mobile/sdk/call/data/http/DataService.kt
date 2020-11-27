package com.mobile.sdk.call.data.http

import com.mobile.sdk.call.data.http.ApiFile
import com.mobile.sdk.call.data.http.ApiSysReply
import com.mobile.sdk.call.data.http.ApiUser
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DataService {

    @GET("/api/member/getToken")
    fun token(
        @Query("comeFrom") from: Int,
        @Query("username") username: String
    ): Call<ApiUser>

    @POST("/api/oss/uploadFiles")
    fun uploadFile(@Body body: RequestBody): Call<ApiFile>

    @GET("/api/sysReply/listSysReply")
    fun sysReply(
        @Query("flag") flag: Int,
        @Query("words") keyword: String
    ): Call<List<ApiSysReply>>
}