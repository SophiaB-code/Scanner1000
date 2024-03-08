package com.example.scanner1000.repository

import com.example.scanner1000.data.Category
import com.example.scanner1000.data.CategoryDao
import com.example.scanner1000.data.Product
import com.example.scanner1000.data.ProductDao

class Repository(
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao
) {

    suspend fun insertCategory(category: Category) {
        categoryDao.upsertCategory(category)
    }

    suspend fun insertProduct(product: Product) {
        productDao.upsertProduct(product)
    }

    suspend fun deleteItem(product: Product) {
        productDao.deleteProduct(product)
    }

}