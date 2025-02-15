package com.abdushakoor12.sawal.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrModelEntityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orModel: OpenRouterModelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orModels: List<OpenRouterModelEntity>)

    @Query("SELECT * FROM openroutermodelentity ORDER BY name ASC")
    suspend fun getAllModels(): List<OpenRouterModelEntity>
}