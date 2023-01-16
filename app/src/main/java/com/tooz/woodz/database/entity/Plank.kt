package com.tooz.woodz.database.entity

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(tableName = "plank",
    foreignKeys = [ForeignKey(
        entity = Material::class,
        childColumns = ["material_id"],
        parentColumns = ["id"]
    )],
    indices = [Index("material_id")])
data class Plank (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @NonNull @ColumnInfo(name = "width") val width: Double,
    @NonNull @ColumnInfo(name = "height") val height: Double,
    @NonNull @ColumnInfo(name = "group") val group: String,
    @NonNull @ColumnInfo(name = "type") val type: String,
    @NonNull @ColumnInfo(name = "material_id") val materialId: Int,
    @NonNull @ColumnInfo(name = "corner_left") val cornerLeft: Double,
    @NonNull @ColumnInfo(name = "corner_right") val cornerRight: Double,
    @NonNull @ColumnInfo(name = "corner_bottom") val cornerBottom: Double,
    @NonNull @ColumnInfo(name = "corner_up") val cornerUp: Double,
    @Nullable @ColumnInfo(name = "barcode") val barcode: String?,
)