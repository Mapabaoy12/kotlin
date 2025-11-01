package com.example.pasteleriamilsabores.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pasteleriamilsabores.data.local.dao.ProductDao
import com.example.pasteleriamilsabores.data.local.dao.CartDao
import com.example.pasteleriamilsabores.data.local.entity.ProductEntity
import com.example.pasteleriamilsabores.data.local.entity.CartItemEntity

@Database(
    entities = [
        ProductEntity::class,
        CartItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "pasteleria_db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()

        suspend fun clearDatabase(context: Context) {
            getInstance(context).clearAllTables()
        }

        suspend fun recreateDatabase(context: Context) {
            context.deleteDatabase("pasteleria_db")
            INSTANCE = null
        }
    }
}