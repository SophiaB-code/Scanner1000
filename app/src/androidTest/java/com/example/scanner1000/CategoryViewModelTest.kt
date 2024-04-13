package com.example.scanner1000

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.scanner1000.data.AppDatabase
import com.example.scanner1000.data.Category
import com.example.scanner1000.data.CategoryDao
import com.example.scanner1000.data.category.CategoryViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var categoryDao: CategoryDao
    private lateinit var viewModel: CategoryViewModel
    private lateinit var database: AppDatabase

    @Before
    fun setupDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()

        categoryDao = database.categoryDao()
        viewModel = CategoryViewModel(categoryDao)
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun getCategoryTitleById_ShouldReturnCorrectTitle() = runTest {
        val categoryId = 1
        val categoryTitle = "Test category"
        categoryDao.upsertCategory(
            Category(
                id = categoryId,
                title = categoryTitle,
                dateAdded = System.currentTimeMillis()
            )
        )
        val flow = viewModel.getCategoryTitleById(categoryId)
        val title = flow.first()
        assertEquals(categoryTitle, title)
    }
}