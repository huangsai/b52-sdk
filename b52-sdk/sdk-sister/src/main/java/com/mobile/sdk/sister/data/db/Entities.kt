package com.mobile.sdk.sister.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "sister_message"
)
data class DbMessage(
    @ColumnInfo(name = "_id") @PrimaryKey(autoGenerate = true) val _id: Long,
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "fromUserId") val fromUserId: Long,
    @ColumnInfo(name = "toUserId") val toUserId: Long,
    @ColumnInfo(name = "status") val status: Int,
    @ColumnInfo(name = "time") val time: Long
)