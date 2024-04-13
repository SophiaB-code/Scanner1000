package com.example.scanner1000

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.scanner1000.data.AppDatabase
import com.example.scanner1000.data.FriendDao
import com.example.scanner1000.data.Product
import com.example.scanner1000.data.ProductDao
import com.example.scanner1000.data.SharedProductDao
import com.example.scanner1000.data.product.ProductViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ProductViewModel
    private lateinit var productDao: ProductDao
    private lateinit var sharedProductDao: SharedProductDao
    private lateinit var friendDao: FriendDao


    private lateinit var database: AppDatabase

    @Before
    fun setupDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        productDao = database.productDao()
        sharedProductDao = database.sharedProductDao()
        friendDao = database.friendDao()
        viewModel = ProductViewModel(productDao, sharedProductDao, friendDao)

    }

    @After
    fun closeDatabase() {
        database.close()
    }
}