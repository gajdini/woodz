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

    @Query("SELECT * FROM  plank WHERE barcode = :barcode LIMIT 1")
    fun getByBarcode(barcode: String): Flow<Plank>

    @Query("SELECT * FROM  plank WHERE material_id = :materialId")
    suspend fun getAllByMaterial(materialId: Int): List<Plank>

    @Query("UPDATE plank SET done = 1 WHERE id = :id")
    suspend fun setDone(id: Int)
}