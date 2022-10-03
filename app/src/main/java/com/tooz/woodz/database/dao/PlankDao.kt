package com.tooz.woodz.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.tooz.woodz.database.entity.Plank
import kotlinx.coroutines.flow.Flow

@Dao
interface PlankDao {
    @Query("SELECT * FROM  plank")
    fun getAll(): Flow<List<Plank>>

    @Query("SELECT * FROM  plank WHERE id = :id")
    fun getById(id: Int): Flow<Plank>

    @Query("SELECT * FROM  plank WHERE material_id = :materialId")
    fun getAllByMaterial(materialId: Int): Flow<List<Plank>>
}