package com.mobile.sdk.ipv6.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mobile.sdk.ipv6.data.api.ApiConfig
import com.squareup.moshi.Json

@Entity(
    tableName = "table_ip"
)
data class DbIp(
    @Json(name = "_id") @PrimaryKey(autoGenerate = true) val _id: Long,
    @Json(name = "url") val url: String,
    @Json(name = "data") val data: String,
    @Json(name = "urlDesc") val urlDesc: String? = "",
    @Json(name = "urlType") val urlType: Int
) {
    fun toIp(): ApiConfig.Ip {
        return ApiConfig.Ip(url, data, urlDesc, urlType)
    }
}

@Entity(
    tableName = "table_task"
)
data class DbTask(
    @Json(name = "_id") @PrimaryKey(autoGenerate = true) val _id: Long,
    @Json(name = "state") val state: Int,

    @Json(name = "taskId") val taskId: String,
    @Json(name = "url") val url: String,
    @Json(name = "requestType") val requestType: String,
    @Json(name = "timeOut") val timeout: Long,
    @Json(name = "header") val header: String,
    @Json(name = "body") val body: String,
    @Json(name = "callbackUrl") val callbackUrl: String
)