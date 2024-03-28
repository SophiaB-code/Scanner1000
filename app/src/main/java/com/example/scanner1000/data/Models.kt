package com.example.scanner1000.data


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "category")
data class Category(
    val title: String,
    val dateAdded: Long,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

@Entity(tableName = "product")
data class Product(
    val name: String,
    val price: Double,
    val dateAdded: Long,
    val categoryFk: Int,
    val isSplit: Boolean = false,
    val isChecked: Boolean = false,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

@Entity(tableName = "friend")
data class Friend(
    val name: String,
    val balance: Double,
    val isChecked: Boolean = false,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

@Entity(tableName = "sharedProductInfo")
data class SharedProductInfo(
    @ColumnInfo(name = "productId")  val productId: Int,
    @ColumnInfo(name = "friendId") val friendId: Int,
    @ColumnInfo(name = "amountPerFriend") val amountPerFriend: Double,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
@Entity(tableName = "refunds")
data class Refund(
    val friendId: Int,
    val amount: Double,
    val description: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

