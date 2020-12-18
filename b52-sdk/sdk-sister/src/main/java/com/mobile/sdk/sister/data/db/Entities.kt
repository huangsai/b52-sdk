package com.mobile.sdk.sister.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(
    tableName = "sister_message",
    indices = [Index(value = ["id"], unique = true)]
)
data class DbMessage(
    @ColumnInfo(name = "_id") @PrimaryKey(autoGenerate = true) val _id: Long,
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "sessionId") val sessionId: String,
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "toUserId") val toUserId: String,
    @ColumnInfo(name = "content") var content: String,
    @ColumnInfo(name = "time") val time: Long,
    @ColumnInfo(name = "fromUserImage") val fromUserImage: String,
    @ColumnInfo(name = "fromUsername") val fromUsername: String,
    @ColumnInfo(name = "fromUserId") val fromUserId: String,
    @ColumnInfo(name = "fromUserType") val fromUserType: Int,
    @ColumnInfo(name = "chatType") val chatType: Int,
    @ColumnInfo(name = "status") var status: Int
) {
    fun asProgressing(): DbMessage {
        status = 1
        return this
    }

    fun asSuccess(): DbMessage {
        status = 2
        return this
    }

    fun asFailed(): DbMessage {
        status = 3
        return this
    }

    fun isProgressing(): Boolean = status == 1

    fun isSuccess(): Boolean = status == 2

    fun isFailed(): Boolean = status == 3

    fun isFromSister(): Boolean = fromUserType == 1

    fun isChargeType(): Boolean = chatType == 1

    @JsonClass(generateAdapter = true)
    data class Text(@Json(name = "msg") val msg: String)

    @JsonClass(generateAdapter = true)
    data class Audio(
        @Json(name = "duration") val duration: Long,
        @Json(name = "msg") val url: String
    )

    @JsonClass(generateAdapter = true)
    data class Image(@Json(name = "msg") val url: String)

    @JsonClass(generateAdapter = true)
    data class System(@Json(name = "msg") val msg: String)

    @JsonClass(generateAdapter = true)
    data class Upgrade(@Json(name = "msg") val msg: String)

    @JsonClass(generateAdapter = true)
    data class Time(@Json(name = "nano") val nano: Long)

    @JsonClass(generateAdapter = true)
    data class Deposit(
        @Json(name = "name") val name: String,
        @Json(name = "type") val type: Int,
        @Json(name = "url") val url: String
    )
}

@Entity(
    tableName = "sister_session",
    indices = [Index(value = ["userId", "sisterId"], unique = true)]
)
data class DbSession(
    @ColumnInfo(name = "_id") @PrimaryKey val _id: String,
    @ColumnInfo(name = "sisterImage") val sisterImage: String,
    @ColumnInfo(name = "sisterName") val sisterName: String,
    @ColumnInfo(name = "sisterId") val sisterId: String,
    @ColumnInfo(name = "userId") val userId: String,
    @ColumnInfo(name = "chatType") val chatType: Int,
    @ColumnInfo(name = "time") var time: Long
) {

    fun isChargeType(): Boolean = chatType == 1
}