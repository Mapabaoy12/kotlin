package com.example.pasteleriamilsabores.data.repo

import com.example.pasteleriamilsabores.data.local.AppDatabase
import com.example.pasteleriamilsabores.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartRepository(private val db: AppDatabase) {
    private val dao = db.cartDao()

    fun getCartFlow(): Flow<List<CartItemEntity>> = dao.getAllFlow()

    suspend fun addToCart(item: CartItemEntity) = withContext(Dispatchers.IO) {
        val existingItem = dao.getByProductId(item.productId)
        if (existingItem != null) {
            dao.updateCantidad(item.productId, existingItem.cantidad + item.cantidad)
        } else {
            dao.insert(item)
        }
    }

    suspend fun updateCantidad(productId: Int, cantidad: Int) = withContext(Dispatchers.IO) {
        dao.updateCantidad(productId, cantidad)
    }

    suspend fun removeFromCart(item: CartItemEntity) = withContext(Dispatchers.IO) {
        dao.delete(item)
    }

    suspend fun clearCart() = withContext(Dispatchers.IO) {
        dao.clearCart()
    }
}
