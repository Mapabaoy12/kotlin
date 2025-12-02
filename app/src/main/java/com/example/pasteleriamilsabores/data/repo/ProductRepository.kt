package com.example.pasteleriamilsabores.data.repo

import android.content.Context
import android.util.Log
import com.example.pasteleriamilsabores.data.local.AppDatabase
import com.example.pasteleriamilsabores.data.local.entity.ProductEntity
import com.example.pasteleriamilsabores.data.model.Product
import com.example.pasteleriamilsabores.data.remote.RetrofitInstance
import com.example.pasteleriamilsabores.util.NetworkUtils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class ProductRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val dao = db.productDao()
    private val api = RetrofitInstance.api
    private val gson = Gson()

    //Super flujo de la UI
    fun getProductsFlow(): Flow<List<Product>> {
        return dao.getAllFlow().map { list ->
            list.map { entity ->
                Product(
                    id = entity.id,
                    sku = entity.sku,
                    titulo = entity.titulo,
                    descripcion = entity.descripcion,
                    precio = entity.precio,
                    imagen = entity.imagen,
                    forma = entity.forma,
                    tamanio = entity.tamanio,
                    stock = entity.stock
                )
            }
        }
    }

    suspend fun getProductById(id: Int): Product? = withContext(Dispatchers.IO){
        dao.getByid(id)?.let { entity ->
            Product(
                id = entity.id,
                sku = entity.sku,
                titulo = entity.titulo,
                descripcion = entity.descripcion,
                precio = entity.precio,
                imagen = entity.imagen,
                forma = entity.forma,
                tamanio = entity.tamanio,
                stock = entity.stock
            )
        }
    }

    suspend fun getProductsFromApi(): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            if (!NetworkUtils.isInternetAvailable(context)) {
                return@withContext Result.failure(Exception("No hay conexión a Internet"))
            }

            val products = api.getProducts()
            
            // Save to local database
            val entities = products.map { p ->
                ProductEntity(
                    id = p.id,
                    sku = p.sku,
                    titulo = p.titulo,
                    descripcion = p.descripcion,
                    precio = p.precio,
                    imagen = p.imagen,
                    forma = p.forma,
                    tamanio = p.tamanio,
                    stock = p.stock
                )
            }
            
            dao.deleteAll()
            dao.insertAll(entities)
            
            Result.success(products)
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error fetching products from API", e)
            Result.failure(e)
        }
    }

    suspend fun getProductsFromLocal(): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val entities = dao.getAll()
            val products = entities.map { entity ->
                Product(
                    id = entity.id,
                    sku = entity.sku,
                    titulo = entity.titulo,
                    descripcion = entity.descripcion,
                    precio = entity.precio,
                    imagen = entity.imagen,
                    forma = entity.forma,
                    tamanio = entity.tamanio,
                    stock = entity.stock
                )
            }
            Result.success(products)
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error fetching products from local database", e)
            Result.failure(e)
        }
    }

    private suspend fun insertEntities(list: List<ProductEntity>) = withContext(Dispatchers.IO){
        dao.insertAll(list)
    }

    sealed class Error : Exception() {
        data class DatabaseError(override val message: String) : Error()
        data class JsonParseError(override val cause: Throwable) : Error()
        data class AssetLoadError(override val cause: Throwable) : Error()
    }

    suspend fun initializeIfNeeded(forceUpdate: Boolean = false) = withContext(Dispatchers.IO) {
        try {
            if (forceUpdate) {
                try {
                    AppDatabase.recreateDatabase(context)
                } catch (e: Exception) {
                    throw Error.DatabaseError("Error al recrear la base de datos: ${e.message}")
                }
            }

            val c = dao.count()
            if(c == 0) {
                // Try to load from assets as fallback
                try {
                    val json = context.assets.open("products.json").bufferedReader().use { it.readText() }
                    val products: List<Product> = gson.fromJson(json, object : com.google.gson.reflect.TypeToken<List<Product>>() {}.type)
                    
                    val entities = products.map { p ->
                        ProductEntity(
                            id = p.id,
                            sku = p.sku,
                            titulo = p.titulo,
                            descripcion = p.descripcion,
                            precio = p.precio,
                            imagen = p.imagen,
                            forma = p.forma,
                            tamanio = p.tamanio,
                            stock = p.stock
                        )
                    }
                    
                    insertEntities(entities)
                } catch (exception: Exception) {
                    // Log the error but don't throw - can use API instead
                    Log.w("ProductRepository", "Could not load products from assets, API will be used instead", exception)
                }
            }
        } catch (error: Error) {
            throw error
        } catch (exception: Exception) {
            throw Error.DatabaseError("Error inesperado: ${exception.message}")
        }
    }
}
