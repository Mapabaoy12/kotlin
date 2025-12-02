package com.example.pasteleriamilsabores.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.pasteleriamilsabores.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    /**
     * Get all products as a Flow for reactive UI updates.
     * Use this for observing product list changes in the UI.
     */
    @Query("SELECT * FROM products ORDER BY id ASC")
    fun getAllFlow(): Flow<List<ProductEntity>>

    /**
     * Get all products as a one-time snapshot.
     * Use this when you need the current list without observing changes,
     * such as when syncing with API or exporting data.
     */
    @Query("SELECT * FROM products ORDER BY id ASC")
    suspend fun getAll(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getByid(id: Int): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    @Update
    suspend fun update(product: ProductEntity)

    @Delete
    suspend fun delete(product: ProductEntity)

    /**
     * Delete all products from the database.
     * Use this before importing fresh data from the API.
     */
    @Query("DELETE FROM products")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int

    /**
     * Replace all products with a new list in a single transaction.
     * This ensures atomicity when syncing with the API.
     */
    @Transaction
    suspend fun replaceAll(products: List<ProductEntity>) {
        deleteAll()
        insertAll(products)
    }

}