package com.abdushakoor12.sawal.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chatEntity: ChatEntity)

    @Delete
    suspend fun delete(chatEntity: ChatEntity)

    @Query("SELECT * FROM chatentity")
    fun getAllChatsFlow(): Flow<List<ChatEntity>>
}