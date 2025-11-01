package com.example.pasteleriamilsabores.data.model
//Va a ser usado por UI y VW helltea
data class Product (
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val precio: Double,
    val imagen: String,
    val forma: String? = null,
    val tamanio: String? = null,
    val stock: Int? = 0
)
