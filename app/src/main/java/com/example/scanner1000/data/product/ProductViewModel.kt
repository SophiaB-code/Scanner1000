package com.example.scanner1000.data.product


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanner1000.data.Product
import com.example.scanner1000.data.ProductDao
import com.example.scanner1000.data.ProductInfoForFriend
import com.example.scanner1000.data.SharedProductDao
import com.example.scanner1000.data.SharedProductInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ProductViewModel(
    private val dao: ProductDao,
    private val sharedProductDao: SharedProductDao
) : ViewModel() {
    private val isSortedByName = MutableStateFlow(true)
    val sumOfCheckedProducts: Flow<Double?> = dao.getSumOfCheckedProducts()


    @OptIn(ExperimentalCoroutinesApi::class)
    var products =
        isSortedByName.flatMapLatest {
                dao.getProductsOrderedByName()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val _state = MutableStateFlow(ProductState())
    val state =
        combine(_state, isSortedByName, products) { state, isSortedByName, products ->
            state.copy(
                products = products
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProductState())

    private val _productsWithCategory = MutableStateFlow<List<Product>>(emptyList())
    val productsWithCategory: StateFlow<List<Product>> = _productsWithCategory


    fun onEvent(event: ProductEvent) {
        when (event) {
            is ProductEvent.DeleteProduct -> {
                viewModelScope.launch {
                    dao.deleteProduct(event.product)
                }
            }

            is ProductEvent.SaveProduct -> {
                val product = Product(
                    name = state.value.name.value,
                    price = state.value.price.value,
                    dateAdded = System.currentTimeMillis(),
                    categoryFk = event.categoryFk,
                    isSplit = state.value.isSplit.value,
                    isChecked = state.value.isChecked.value,

                )

                viewModelScope.launch {
                    dao.upsertProduct(product)
                }

                _state.update {
                    it.copy(
                        name = mutableStateOf(""),
                        price = mutableStateOf(00.00)
                    )
                }
            }
            is ProductEvent.EditProduct -> {
                viewModelScope.launch {
                    dao.updateProduct(event.product) // Załóżmy, że masz taką metodę
                }

            }


            ProductEvent.SortProducts -> {
                isSortedByName.value = !isSortedByName.value
            }

            else -> {}
        }
    }

    fun getProductsWithCategory(categoryId: Int) {
        viewModelScope.launch {
            dao.getProductsWithCategory(categoryId).collect { products ->
                _productsWithCategory.value = products
                }
            }
        }

    fun setProductChecked(product: Product, isChecked: Boolean) = viewModelScope.launch {
        dao.updateProductIsChecked(product.id, isChecked)
    }

    fun setNotSplitProductsChecked(isChecked: Boolean) = viewModelScope.launch {
        dao.setNotSplitProductsChecked(isChecked)
    }

    fun updateProductsAsSplit() = viewModelScope.launch {
        dao.updateProductsAsSplit()
    }
    fun resetProductsCheckedStatus() {
        viewModelScope.launch {
            dao.resetProductsCheckedStatus()
        }
    }
    fun addSharedProductInfo(productId: Int, friendId: Int, amountPerFriend: Double) = viewModelScope.launch {
        val newSharedProduct = SharedProductInfo(productId = productId, amountPerFriend = amountPerFriend, friendId = friendId)
        sharedProductDao.insertSharedProduct(newSharedProduct)
    }
    fun getProductNameById(productId: Int): Flow<String> {
        return dao.getProductNameById(productId)
    }
    fun getGroupedByFriendSharedProducts(friendId: Int): Flow<List<SharedProductInfo>> {
        return sharedProductDao.getGroupedByFriendSharedProduct(friendId)
    }


    val checkedProductIds: Flow<List<Int>> = dao.getCheckedProductsIds()

    fun addProduct(product: Product) = viewModelScope.launch {
        dao.upsertProduct(product)
    }

    fun deleteProduct(product: Product) = viewModelScope.launch {
        dao.deleteProduct(product)
    }
    fun getProductInfoForFriend(friendId: Int): Flow<List<ProductInfoForFriend>> {
        return sharedProductDao.getProductInfoForFriend(friendId)
    }

    suspend fun createProductAndReturnId(product: Product): Int {
        dao.updateProduct(product)
        // Tu zakładamy, że `insertProduct` zwraca ID nowo utworzonego produktu
        return dao.getLastInsertedProductId()
    }


}


