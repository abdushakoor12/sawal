package com.abdushakoor12.sawal.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OpenRouterModelEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val pricePerPrompt: String,
    val pricePerCompletion: String,
    val pricePerImage: String,
    val pricePerRequest: String,
)