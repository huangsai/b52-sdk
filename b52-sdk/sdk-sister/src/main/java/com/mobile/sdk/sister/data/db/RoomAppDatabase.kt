package com.mobile.sdk.sister.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        DbMessage::class
    ],
    version = 1,
    exportSchema = true
)
abstract class RoomAppDatabase : RoomDatabase(), AppDatabase {

    class DbCallback : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
        }
    }

    class DbMigration : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
        }
    }
}