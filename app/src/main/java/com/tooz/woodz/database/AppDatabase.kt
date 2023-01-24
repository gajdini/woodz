package com.tooz.woodz.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tooz.woodz.database.dao.MachineDao
import com.tooz.woodz.database.dao.MaterialDao
import com.tooz.woodz.database.dao.PlankDao
import com.tooz.woodz.database.dao.ProjectDao
import com.tooz.woodz.database.entity.Machine
import com.tooz.woodz.database.entity.Material
import com.tooz.woodz.database.entity.Plank
import com.tooz.woodz.database.entity.Project

@Database(entities = [Project::class, Material::class, Plank::class, Machine::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun materialDao(): MaterialDao
    abstract fun plankDao(): PlankDao
    abstract fun machineDao(): MachineDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .createFromAsset("database/woodz.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}