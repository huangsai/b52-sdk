package com.mobile.sdk.sister.data.db

interface AppDatabase {

    fun messageDao(): MessageDao

    fun sessionDao(): SessionDao
}