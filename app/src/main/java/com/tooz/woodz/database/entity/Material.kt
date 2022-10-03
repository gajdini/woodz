package com.tooz.woodz.database.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "material",
    foreignKeys = [ForeignKey(
        entity = Project::class,
        childColumns = ["project_id"],
        parentColumns = ["id"]
    )])
data class Material (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @NonNull @ColumnInfo(name = "name") val name: String,
    @NonNull @ColumnInfo(name = "quantity", defaultValue = "1") val quantity: Int,
    @NonNull @ColumnInfo(name = "project_id") val projectId: Int
)