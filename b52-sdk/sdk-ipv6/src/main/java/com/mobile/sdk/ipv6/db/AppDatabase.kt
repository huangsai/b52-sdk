package com.mobile.sdk.ipv6.db

interface AppDatabase {

    fun ipDao(): IpDao
    
    fun taskDao(): TaskDao
}