package com.example.scanner1000.data.product


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanner1000.data.FriendDao
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ProductViewModel(
    private val productDao: ProductDao,
    private val sharedProductDao: SharedProductDao,
    private val friendDao: FriendDao
) : ViewModel() {
    private val isSortedByName = MutableStateFlow(true)
    val sumOfCheckedProducts: Flow<Double?> = productDao.getSumOfCheckedProducts()
    val priceOfCheckedProduct: Flow<Double?> = productDao.getPriceOfCheckedProduct()
    val checkedProductIds: Flow<List<Int>> = productDao.getCheckedProductsIds()

    @OptIn(ExperimentalCoroutinesApi::class)
    var products =
        isSortedByName.flatMapLatest {
            productDao.getProductsOrderedByName()
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
            is ProductEvent.DeleteProduct -> deleteProductAndRefundFriends(event.product)

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
                    productDao.upsertProduct(product)
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
                    productDao.updateProduct(event.product) // Załóżmy, że masz taką metodę
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
            productDao.getProductsWithCategory(categoryId).collect { products ->
                _productsWithCategory.value = products
            }
        }
    }

    fun setProductChecked(product: Product, isChecked: Boolean) = viewModelScope.launch {
        productDao.updateProductIsChecked(product.id, isChecked)
    }

    fun setNotSplitProductsChecked(isChecked: Boolean) = viewModelScope.launch {
        productDao.setNotSplitProductsChecked(isChecked)
    }

    fun updateProductsAsSplit() = viewModelScope.launch {
        productDao.updateProductsAsSplit()
    }

    fun resetProductsCheckedStatus() {
        viewModelScope.launch {
            productDao.resetProductsCheckedStatus()
        }
    }

    fun addSharedProductInfo(productId: Int, friendId: Int, amountPerFriendPerProduct: Double) =
        viewModelScope.launch {
            val newSharedProduct = SharedProductInfo(
                productId = productId,
                amountPerFriend = amountPerFriendPerProduct,
                friendId = friendId
            )
            sharedProductDao.insertSharedProduct(newSharedProduct)
        }


    fun addProduct(product: Product) = viewModelScope.launch {
        productDao.upsertProduct(product)
    }

    fun getProductInfoForFriend(friendId: Int): Flow<List<ProductInfoForFriend>> {
        return sharedProductDao.getProductInfoForFriend(friendId)
    }

    private fun deleteProductAndRefundFriends(product: Product) = viewModelScope.launch {
        // Usunięcie produktu
        productDao.deleteProductById(product.id)

        // Pobranie wszystkich powiązanych informacji o podziale produktu
        val sharedInfoList = sharedProductDao.getSharedProductsByProductId(product.id).first()

        // Usunięcie powiązanych informacji o podziale produktu
        sharedProductDao.deleteSharedProductsByProductId(product.id)

        // Aktualizacja bilansu przyjaciół na podstawie usuniętych informacji o podziale produktu
        sharedInfoList.forEach { sharedInfo ->
            val currentBalance = friendDao.getFriendBalanceById(sharedInfo.friendId)
            val newBalance = currentBalance + sharedInfo.amountPerFriend
            friendDao.updateFriendBalance(sharedInfo.friendId, newBalance)
        }
    }

    fun undoProductSplit(product: Product) = viewModelScope.launch {
        // Zmiana statusu isSplit na false
        val updatedProduct = product.copy(isSplit = false)
        productDao.updateProduct(updatedProduct)

        // Pobranie wszystkich powiązanych informacji o podziale produktu
        val sharedInfoList = sharedProductDao.getSharedProductsByProductId(product.id).first()

        // Usunięcie powiązanych informacji o podziale produktu
        sharedProductDao.deleteSharedProductsByProductId(product.id)

        // Aktualizacja bilansu przyjaciół na podstawie usuniętych informacji o podziale produktu
        sharedInfoList.forEach { sharedInfo ->
            val currentBalance = friendDao.getFriendBalanceById(sharedInfo.friendId)
            val newBalance = currentBalance + sharedInfo.amountPerFriend
            friendDao.updateFriendBalance(sharedInfo.friendId, newBalance)
        }
    }
}


