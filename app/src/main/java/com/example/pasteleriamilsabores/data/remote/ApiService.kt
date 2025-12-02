package com.example.pasteleriamilsabores.data.remote

import com.example.pasteleriamilsabores.data.model.Product
import retrofit2.http.GET

interface ApiService {
    @GET("chalalo1533/ServicioRest/refs/heads/master/productos.json")
    suspend fun getProducts(): List<Product>
}
