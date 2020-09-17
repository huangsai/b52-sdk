package com.mobile.sdk.sister.data.db

import androidx.room.Dao
import androidx.room.Query
import com.mobile.guava.https.SqlDao

@Dao
interface MessageDao : SqlDao<DbMessage> {

    @Query("SELECT * FROM sister_message ORDER BY time DESC")
    fun get(): List<DbMessage>

    @Query("SELECT * FROM sister_message WHERE id =:id ORDER BY time DESC")
    fun getById(id: String): DbMessage?

    @Query("SELECT * FROM sister_message WHERE type =:type ORDER BY time DESC")
    fun getByType(type: Int): List<DbMessage>

    @Query("SELECT * FROM sister_message WHERE type =:status ORDER BY time DESC")
    fun getByStatus(status: Int): List<DbMessage>

    @Query("SELECT * FROM sister_message WHERE toUserId =:userId OR fromUserId=:userId ORDER BY time DESC")
    fun getByUserId(userId: Long): List<DbMessage>

    @Query("DELETE FROM sister_message WHERE toUserId =:userId OR fromUserId=:userId")
    fun clearByUserId(userId: Long): Int

    @Query("DELETE FROM sister_message")
    fun clear(): Int
}