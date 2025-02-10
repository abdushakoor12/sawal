package com.abdushakoor12.sawal.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chatMessage: ChatMessageEntity)

    @Delete
    suspend fun delete(chatMessage: ChatMessageEntity)

    @Query("SELECT * FROM chatmessageentity WHERE chatId = :chatId ORDER BY createdAt DESC")
    fun getAllMessagesFlow(chatId: String): Flow<List<ChatMessageEntity>>
}