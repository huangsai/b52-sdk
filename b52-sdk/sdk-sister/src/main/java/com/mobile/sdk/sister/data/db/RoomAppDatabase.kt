package com.mobile.sdk.sister.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        DbMessage::class,
        DbSession::class
    ],
    version = 1,
    exportSchema = true
)
abstract class RoomAppDatabase : RoomDatabase(), AppDatabase {

    class DbCallback : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
        }
    }

    class DbMigration : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
        }
    }
}