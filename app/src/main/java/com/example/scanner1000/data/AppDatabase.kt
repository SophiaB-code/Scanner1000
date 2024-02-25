package com.example.scanner1000.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Product::class, Category::class],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase: RoomDatabase(){
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
}