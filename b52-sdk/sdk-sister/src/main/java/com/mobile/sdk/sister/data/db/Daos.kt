package com.mobile.sdk.sister.data.db

import androidx.room.Dao
import androidx.room.Query
import com.mobile.guava.https.SqlDao
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao : SqlDao<DbMessage> {

    @Query("SELECT * FROM sister_message ORDER BY time DESC")
    fun get(): Flow<List<DbMessage>>

    @Query("SELECT * FROM sister_message WHERE type =:type ORDER BY time DESC")
    fun getByType(type: Int): Flow<List<DbMessage>>

    @Query("SELECT * FROM sister_message WHERE type =:status ORDER BY time DESC")
    fun getByStatus(status: Int): Flow<List<DbMessage>>

    @Query("SELECT * FROM sister_message WHERE toUserId =:userId OR fromUserId=:userId ORDER BY time DESC")
    fun getByUserId(userId: Long): Flow<List<DbMessage>>

    @Query("DELETE FROM sister_message WHERE toUserId =:userId OR fromUserId=:userId")
    fun clearByUserId(userId: Long): Int

    @Query("DELETE FROM sister_message")
    fun clear(): Int
}