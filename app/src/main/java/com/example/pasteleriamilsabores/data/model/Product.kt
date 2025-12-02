package com.example.pasteleriamilsabores.data.model

import com.google.gson.annotations.SerializedName

//Va a ser usado por UI y VW helltea
data class Product (
    @SerializedName("id") val id: Int,
    @SerializedName("sku") val sku: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("precio") val precio: Double,
    @SerializedName("imagen") val imagen: String,
    @SerializedName("forma") val forma: String? = null,
    @SerializedName("tamanio") val tamanio: String? = null,
    @SerializedName("stock") val stock: Int? = 0
)
