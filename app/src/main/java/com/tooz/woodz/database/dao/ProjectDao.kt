package com.tooz.woodz.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.tooz.woodz.database.entity.Project
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM  project ORDER BY name ASC")
    fun getAll(): Flow<List<Project>>
}