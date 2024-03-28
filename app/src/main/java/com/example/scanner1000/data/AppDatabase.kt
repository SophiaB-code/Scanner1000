package com.example.scanner1000.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Product::class, Category::class, Friend::class, SharedProductInfo::class, Refund::class],
    version = 1,
    exportSchema = false
)


abstract class AppDatabase: RoomDatabase(){
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun friendDao():FriendDao
    abstract fun sharedProductDao(): SharedProductDao
    abstract fun refundDao(): RefundDao

    companion object{
        @Volatile
        var INSTANCE:AppDatabase? = null
        fun getDatabase(context: Context):AppDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "appDatabase.db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }

    }

}