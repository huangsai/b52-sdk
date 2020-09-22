package com.mobile.sdk.sister.data.db

import androidx.room.Dao
import androidx.room.Query
import com.mobile.guava.https.SqlDao

@Dao
interface MessageDao : SqlDao<DbMessage> {

    @Query("SELECT * FROM sister_message ORDER BY time DESC")
    fun get(): List<DbMessage>

    @Query("SELECT COUNT(*) FROM sister_message WHERE id =:id")
    fun countById(id: String): Int

    @Query("SELECT * FROM sister_message WHERE id =:id ORDER BY time ASC")
    fun getById(id: String): DbMessage?

    @Query("SELECT * FROM sister_message WHERE type =:type ORDER BY time ASC")
    fun getByType(type: Int): List<DbMessage>

    @Query("SELECT * FROM sister_message WHERE type =:status ORDER BY time ASC")
    fun getByStatus(status: Int): List<DbMessage>

    @Query("SELECT * FROM sister_message WHERE toUserId =:userId OR fromUserId=:userId ORDER BY time ASC")
    fun getByUserId(userId: String): List<DbMessage>

    @Query("DELETE FROM sister_message WHERE toUserId =:userId OR fromUserId=:userId")
    fun clearByUserId(userId: Long): Int

    @Query("DELETE FROM sister_message")
    fun clear(): Int
}