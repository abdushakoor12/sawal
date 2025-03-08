package com.abdushakoor12.sawal.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ChatEntity::class, ChatMessageEntity::class, OpenRouterModelEntity::class, CharacterEntity::class],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chatEntityDao(): ChatEntityDao
    abstract fun chatMessageEntityDao(): ChatMessageEntityDao
    abstract fun orModelEntityDao(): OrModelEntityDao
    abstract fun characterEntityDao(): CharacterEntityDao

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