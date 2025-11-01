package com.example.pasteleriamilsabores.data.local.dao

import androidx.room.*
import com.example.pasteleriamilsabores.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getAllFlow(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItemEntity)

    @Delete
    suspend fun delete(item: CartItemEntity)

    @Query("UPDATE cart_items SET cantidad = :cantidad WHERE productId = :productId")
    suspend fun updateCantidad(productId: Int, cantidad: Int)

    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun getByProductId(productId: Int): CartItemEntity?

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}
