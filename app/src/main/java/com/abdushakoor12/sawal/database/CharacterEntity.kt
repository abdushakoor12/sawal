package com.abdushakoor12.sawal.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity
data class CharacterEntity(
    @PrimaryKey
    val uuid: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
) : Parcelable 