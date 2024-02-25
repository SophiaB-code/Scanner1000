package com.example.scanner1000.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Upsert
    suspend fun upsertCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM category ORDER BY dateAdded")
    fun getCategoryOrderedByDateAdded(): Flow<List<Category>>


}

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProduct(category: Product)

    @Delete
    suspend fun deleteProduct(category: Product)



    @Query("SELECT * FROM product ORDER BY name ASC")
    fun getProductsOrderedByName(): Flow<List<Product>>

}

