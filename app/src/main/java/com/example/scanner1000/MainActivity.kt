package com.example.scanner1000

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.scanner1000.data.AppDatabase
import com.example.scanner1000.data.category.CategoryViewModel
import com.example.scanner1000.data.product.ProductViewModel
import com.example.scanner1000.ui.screens.MainScreen
import com.example.scanner1000.ui.screens.ReceiptsScreen
import com.example.scanner1000.ui.screens.TextRecognitionScreen
import com.example.scanner1000.ui.theme.Scanner1000Theme

class MainActivity : ComponentActivity() {

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "appDatabase.db"
        ).build()
    }

    private val viewModelCat by viewModels<CategoryViewModel> (
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun<T: ViewModel> create(modelClass: Class<T>): T {
                    return CategoryViewModel(database.categoryDao()) as T
                }
            }
        }
    )

    private val viewModelPro by viewModels<ProductViewModel> (
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun<T: ViewModel> create(modelClass: Class<T>): T {
                    return ProductViewModel(database.productDao()) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scanner1000Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController= navController, startDestination = "MainScreen") {
                        composable("mainScreen") {
                            MainScreen(
                                navController
                            )
                        }
                        composable("receiptsScreen") {
                            ReceiptsScreen(
                                viewModelCat
                            )
                        }
                        composable("textRecognitionScreen") {
                            TextRecognitionScreen(
                                navController = navController,
                                viewModelCat
                            )
                        }
                    }
                }
            }
        }
    }
}

