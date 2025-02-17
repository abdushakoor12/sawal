package com.abdushakoor12.sawal.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class ChatMessageEntity(
    @PrimaryKey
    val uuid: String = UUID.randomUUID().toString(),
    val chatId: String,
    val message: String,
    var role: String,
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(defaultValue = "false")
    val fav: Boolean = false,
)