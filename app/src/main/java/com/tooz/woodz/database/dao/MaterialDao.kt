package com.tooz.woodz.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.tooz.woodz.database.entity.Material
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {
    @Query("SELECT * FROM  material ORDER BY name ASC")
    fun getAll(): Flow<List<Material>>

    @Query("SELECT * FROM  material WHERE project_id = :projectId ORDER BY name ASC")
    fun getAllByProject(projectId: Int): Flow<List<Material>>
}