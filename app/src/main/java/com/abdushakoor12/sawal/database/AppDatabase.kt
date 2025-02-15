package com.abdushakoor12.sawal.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ChatEntity::class, ChatMessageEntity::class, OpenRouterModelEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chatEntityDao(): ChatEntityDao
    abstract fun chatMessageEntityDao(): ChatMessageEntityDao
    abstract fun orModelEntityDao(): OrModelEntityDao

    companion object {
        private const val DATABASE_NAME = "sawal.db"

        fun getInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}