package com.example.pasteleriamilsabores.data.repo

import android.content.Context
import com.example.pasteleriamilsabores.data.local.AppDatabase
import com.example.pasteleriamilsabores.data.local.entity.ProductEntity
import com.example.pasteleriamilsabores.data.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class ProductRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private var dao = db.productDao()
    private val gson = Gson()

    //Super flujo de la UI
    fun getProductsFlow(): Flow<List<Product>> {
        return dao.getAllFlow().map { list ->
            list.map { entity ->
                Product(
                    id = entity.id,
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
                    val newDb = AppDatabase.getInstance(context)
                    dao = newDb.productDao()
                } catch (e: Exception) {
                    throw Error.DatabaseError("Error al recrear la base de datos: ${e.message}")
                }
            }

            val c = dao.count()
            if(c == 0 || forceUpdate) {
                val json = try {
                    context.assets.open("products.json").bufferedReader().use { it.readText() }
                } catch (exception: Exception) {
                    throw Error.AssetLoadError(exception)
                }

                val products: List<Product> = try {
                    gson.fromJson(json, object : TypeToken<List<Product>>() {}.type)
                } catch (exception: Exception) {
                    throw Error.JsonParseError(exception)
                }

                val entities = products.map { p ->
                    ProductEntity(
                        id = p.id,
                        titulo = p.titulo,
                        descripcion = p.descripcion,
                        precio = p.precio,
                        imagen = p.imagen,
                        forma = p.forma,
                        tamanio = p.tamanio,
                        stock = p.stock
                    )
                }

                try {
                    insertEntities(entities)
                } catch (exception: Exception) {
                    throw Error.DatabaseError("Error al insertar productos en la base de datos: ${exception.message}")
                }
            }
        } catch (error: Error) {
            throw error
        } catch (exception: Exception) {
            throw Error.DatabaseError("Error inesperado: ${exception.message}")
        }
    }
}
