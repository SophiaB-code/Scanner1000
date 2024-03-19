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

    @Update
    suspend fun updateCategory(category: Category)

    @Query("SELECT * FROM category ORDER BY dateAdded")
    fun getCategoryOrderedByDateAdded(): Flow<List<Category>>


}

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("UPDATE product SET isChecked = :isChecked WHERE id = :productId")
    suspend fun updateProductIsChecked(productId: Int, isChecked: Boolean)

    @Query("SELECT * FROM product ORDER BY name ASC")
    fun getProductsOrderedByName(): Flow<List<Product>>

    @Query("""  SELECT * FROM product 
    WHERE isSplit = 1
    ORDER BY name ASC """)
    fun getSplitProducts(): Flow<List<Product>>

    @Query("""  SELECT * FROM product 
    WHERE isSplit = 0
    ORDER BY name ASC """)
    fun getNotSplitProducts(): Flow<List<Product>>

    @Query("""  SELECT * FROM product 
    WHERE categoryFk = :categoryId 
    ORDER BY name ASC """)
    fun getProductsWithCategory(categoryId:Int): Flow<List<Product>>

    @Query("SELECT SUM(price) FROM product WHERE isChecked = 1")
    fun getSumOfCheckedProducts(): Flow<Double?>


}
@Dao
interface FriendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: Friend)

    @Update
    suspend fun updateFriend(friend: Friend)

    @Delete
    suspend fun deleteFriend(friend: Friend)

    @Query("SELECT * FROM friend ORDER BY name ASC")
    fun getAllFriends(): Flow<List<Friend>>

    @Query("SELECT * FROM friend WHERE id = :friendId")
    fun getFriendById(friendId: Int): Flow<Friend>

    @Query("UPDATE friend SET isChecked = :isChecked WHERE id = :friendId")
    suspend fun updateFriendIsChecked(friendId: Int, isChecked: Boolean)

    @Query("SELECT COUNT(*) FROM friend WHERE isChecked = 1")
    fun getCheckedFriendsCount(): Flow<Int>

}
