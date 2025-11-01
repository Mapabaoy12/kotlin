package com.example.pasteleriamilsabores.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val cantidad: Int,
    val precio: Double,
    val titulo: String,
    val imagen: String
)
