package com.example.scanner1000

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.scanner1000.data.AppDatabase
import com.example.scanner1000.data.category.CategoryViewModel
import com.example.scanner1000.data.friend.FriendViewModel
import com.example.scanner1000.data.product.ProductViewModel
import com.example.scanner1000.ui.screens.ChooseCategoryScreen
import com.example.scanner1000.ui.screens.ChoosePhotoScreen
import com.example.scanner1000.ui.screens.MainScreen
import com.example.scanner1000.ui.screens.ProductsWithCategoryScreen
import com.example.scanner1000.ui.screens.ReceiptsScreen
import com.example.scanner1000.ui.screens.TextRecognitionScreen
import com.example.scanner1000.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "appDatabase.db"
        ).build()
    }

    private val viewModelCat by viewModels<CategoryViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CategoryViewModel(database.categoryDao()) as T
                }
            }
        }
    )

    private val viewModelPro by viewModels<ProductViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProductViewModel(database.productDao()) as T
                }
            }
        }
    )

    private val viewModelText by viewModels<TextRecognizingViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TextRecognizingViewModel(application) as T
                }
            }
        }
    )

    private val viewModelFriend by viewModels<FriendViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FriendViewModel(database.friendDao()) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                    // color = md_theme_light_background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "MainScreen") {
                        composable("mainScreen") {
                            MainScreen(
                                navController
                            )
                        }
                        composable("receiptsScreen") {
                            ReceiptsScreen(
                                viewModelCat,
                                viewModelPro,
                                navController
                            )
                        }
                        composable(
                            "textRecognitionScreen/{categoryId}",
                            arguments = listOf(navArgument("categoryId") {
                                type = NavType.IntType
                            })
                        ) { backStackEntry ->
                            TextRecognitionScreen(
                                navController = navController,
                                textRecognizingViewModel = viewModelText,
                                selectedCategoryId = backStackEntry.arguments?.getInt("categoryId")
                            )
                        }
                        composable("chooseCategoryScreen") {
                            ChooseCategoryScreen(viewModelCat, navController)
                        }
                        composable(
                            "choosePhotoScreen/{categoryId}",
                            arguments = listOf(navArgument("categoryId") {
                                type = NavType.IntType
                            })
                        ) { backStackEntry ->
                            ChoosePhotoScreen(
                                navController = navController,
                                textRecognizingViewModel = viewModelText,
                                selectedCategoryId = backStackEntry.arguments?.getInt("categoryId")
                            )
                        }
                        composable(
                            "productsWithCategoryScreen/{category.id}",
                            arguments = listOf(navArgument("category.id") {
                                type = NavType.IntType
                            })
                        ) { backStackEntry ->
                            backStackEntry.arguments?.let {
                                ProductsWithCategoryScreen(
                                    categoryId = it.getInt("category.id"),
                                    viewModelPro,
                                    viewModelFriend
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}

