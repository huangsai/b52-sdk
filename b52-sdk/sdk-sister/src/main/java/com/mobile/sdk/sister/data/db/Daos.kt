package com.mobile.sdk.sister.data.db

import androidx.room.Dao
import androidx.room.Query
import com.mobile.guava.data.SqlDao

@Dao
interface MessageDao : SqlDao<DbMessage> {

    @Query("SELECT COUNT(*) FROM sister_message WHERE id =:id")
    fun countById(id: String): Int

    @Query("SELECT * FROM sister_message WHERE id =:id")
    fun getById(id: String): DbMessage?

    @Query("SELECT * FROM sister_message WHERE toUserId =:userId OR fromUserId=:userId ORDER BY time ASC")
    fun getByUserId(userId: String): List<DbMessage>

    @Query("DELETE FROM sister_message WHERE toUserId =:userId OR fromUserId=:userId")
    fun clearByUserId(userId: Long): Int

    @Query("SELECT * FROM sister_message WHERE sessionId =:sessionId ORDER BY time ASC")
    fun getBySessionId(sessionId: String): List<DbMessage>

    @Query("DELETE FROM sister_message")
    fun clear(): Int
}

@Dao
interface SessionDao : SqlDao<DbSession> {

    @Query("SELECT * FROM sister_session WHERE userId =:userId ORDER BY time ASC")
    fun getByUserId(userId: String): List<DbSession>

    @Query("DELETE FROM sister_session")
    fun clear(): Int
}