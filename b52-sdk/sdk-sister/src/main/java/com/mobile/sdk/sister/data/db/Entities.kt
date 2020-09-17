package com.mobile.sdk.sister.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mobile.sdk.sister.data.http.ApiMessage

@Entity(
    tableName = "sister_message"
)
data class DbMessage(
    @ColumnInfo(name = "_id") @PrimaryKey(autoGenerate = false) val _id: String,
    val type: Int,
    val toUserId: Long,
    val content: String,
    val time: Long,
    val fromUserImage: String,
    val fromUsername: String,
    val fromUserId: Long,
    val fromUserType: Int,
    val status: Int
) {
    fun toApiMessage(): ApiMessage {
        return ApiMessage(
            _id,
            type,
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
