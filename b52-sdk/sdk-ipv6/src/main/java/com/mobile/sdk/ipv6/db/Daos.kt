package com.mobile.sdk.ipv6.db

import androidx.room.Dao
import androidx.room.Query
import com.mobile.guava.data.SqlDao

@Dao
interface IpDao : SqlDao<DbIp> {

    @Query("SELECT * FROM table_ip WHERE _id =:id")
    fun get(id: String): DbIp?

    @Query("SELECT * FROM table_ip")
    fun getAll(): List<DbIp>

    @Query("SELECT * FROM table_ip WHERE urlType =:type")
    fun getAllOfType(type: Int): List<DbIp>

    @Query("DELETE FROM table_ip")
    fun clear(): Int
}

@Dao
interface TaskDao : SqlDao<DbTask> {

    @Query("SELECT * FROM table_task")
    fun getAll(): List<DbTask>

    @Query("DELETE FROM table_task")
    fun clear(): Int
}