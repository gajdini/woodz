package com.tooz.woodz.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.tooz.woodz.database.entity.Machine
import kotlinx.coroutines.flow.Flow

@Dao
interface MachineDao {
    @Query("SELECT * FROM  machine ORDER BY id ASC")
    fun getAll(): Flow<List<Machine>>

    @Query("SELECT address FROM  machine ORDER BY id ASC")
    fun getAllAddresses(): Flow<List<String>>

    @Query("SELECT id FROM  machine WHERE address = :address LIMIT 1")
    fun getIdByAddress(address: String): Flow<Int>
}