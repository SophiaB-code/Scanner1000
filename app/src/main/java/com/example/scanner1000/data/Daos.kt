package com.example.scanner1000.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Query("SELECT * FROM category ORDER BY dateAdded")
    fun getCategoryOrderedByDateAdded(): Flow<List<Category>>


    @Query("SELECT title FROM category WHERE id = :categoryId")
    fun getCategoryTitleById(categoryId: Int): Flow<String>

    @Query("SELECT * FROM category WHERE id = :id")
    suspend fun getCategoryById(id: Int): Category?


}

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)


    @Query("UPDATE product SET isChecked = :isChecked WHERE id = :productId")
    suspend fun updateProductIsChecked(productId: Int, isChecked: Boolean)

    @Query("SELECT * FROM product ORDER BY name ASC")
    fun getProductsOrderedByName(): Flow<List<Product>>

    @Query(
        """  SELECT * FROM product 
    WHERE isSplit = 1
    ORDER BY name ASC """
    )
    fun getSplitProducts(): Flow<List<Product>>

    @Query(
        """  SELECT * FROM product 
    WHERE isSplit = 0
    ORDER BY name ASC """
    )
    fun getNotSplitProducts(): Flow<List<Product>>

    @Query(
        """  SELECT * FROM product 
    WHERE categoryFk = :categoryId 
    ORDER BY name ASC """
    )
    fun getProductsWithCategory(categoryId: Int): Flow<List<Product>>

    @Query("SELECT SUM(price) FROM product WHERE isChecked = 1")
    fun getSumOfCheckedProducts(): Flow<Double?>

    @Query("SELECT id FROM product WHERE isChecked = 1")
    fun getCheckedProductsIds(): Flow<List<Int>>

    @Query("UPDATE product SET isSplit = 1 WHERE isChecked = 1")
    suspend fun updateProductsAsSplit()

    @Query("UPDATE product SET isChecked = 0")
    suspend fun resetProductsCheckedStatus()

    @Query("UPDATE product SET isChecked = :isChecked WHERE isSplit = 0")
    suspend fun setNotSplitProductsChecked(isChecked: Boolean)


}

@Dao
interface FriendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: Friend)

    @Update
    suspend fun updateFriend(friend: Friend)

    @Query("UPDATE friend SET balance = :newBalance WHERE id = :friendId")
    suspend fun updateFriendsBalance(friendId: Int, newBalance: Double)

    @Query("UPDATE friend SET balance = balance + :amountToAdd")
    suspend fun increaseBalance(amountToAdd: Double)

    @Query("UPDATE friend SET balance = balance - :amountToSubtract WHERE isChecked = 1")
    suspend fun decreaseBalanceForCheckedFriends(amountToSubtract: Double)

    @Query("UPDATE friend SET balance = balance - :amountToSubtract")
    suspend fun decreaseBalance(amountToSubtract: Double)

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

    @Query("SELECT * FROM friend WHERE isChecked = 1")
    fun getCheckedFriends(): Flow<List<Friend>>

    @Query(
        """  SELECT id FROM friend 
    WHERE isChecked = 1
    """
    )
    fun getCheckedFriendsIds(): Flow<List<Int>>

    @Query("SELECT * FROM friend WHERE name = :name")
    suspend fun findFriendByName(name: String): Friend?
}

@Dao
interface SharedProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSharedProduct(sharedProduct: SharedProductInfo)

    @Query("SELECT * FROM sharedProductInfo WHERE productId = :productId")
    fun getSharedProductsByProductId(productId: Int): Flow<List<SharedProductInfo>>

    @Query("DELETE FROM sharedProductInfo WHERE productId = :productId")
    suspend fun deleteSharedProductsByProductId(productId: Int)

}


