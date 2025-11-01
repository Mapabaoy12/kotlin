package com.example.pasteleriamilsabores.data.model

data class CartItem(
    val productId: Int,
    val cantidad: Int,
    val precio: Double,
    val titulo: String,
    val imagen: String
)

data class Cart(
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0
)
