package com.example.pasteleriamilsabores.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "titulo") val titulo: String,
    @ColumnInfo(name = "descripcion") val descripcion: String,
    @ColumnInfo(name = "precio") val precio: Double,
    @ColumnInfo(name = "imagen") val imagen: String,
    @ColumnInfo(name = "forma") val forma: String?,
    @ColumnInfo(name = "tamanio") val tamanio: String?,
    @ColumnInfo(name = "stock") val stock: Int? = 0


    )