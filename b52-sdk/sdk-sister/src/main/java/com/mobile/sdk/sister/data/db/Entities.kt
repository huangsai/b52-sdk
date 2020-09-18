package com.mobile.sdk.sister.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mobile.sdk.sister.data.http.ApiMessage

@Entity(
    tableName = "sister_message"
)
data class DbMessage(
    @ColumnInfo(name = "_id") @PrimaryKey(autoGenerate = true) val _id: Long,
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "toUserId") val toUserId: Long,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "time") val time: Long,
    @ColumnInfo(name = "fromUserImage") val fromUserImage: String,
    @ColumnInfo(name = "fromUsername") val fromUsername: String,
    @ColumnInfo(name = "fromUserId") val fromUserId: Long,
    @ColumnInfo(name = "fromUserType") val fromUserType: Int,
    @ColumnInfo(name = "status") var status: Int
) {
    fun toApiMessage(): ApiMessage {
        return ApiMessage(
            id,
            toUserId,
            content,
            time,
            fromUserImage,
            fromUsername,
            fromUserId,
            fromUserType
        )
    }
}
