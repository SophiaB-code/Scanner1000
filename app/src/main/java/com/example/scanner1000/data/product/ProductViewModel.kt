package com.example.scanner1000.data.product


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanner1000.data.Product
import com.example.scanner1000.data.ProductDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ProductViewModel(
    private val dao: ProductDao
) : ViewModel() {
    private val isSortedByName = MutableStateFlow(true)

    @OptIn(ExperimentalCoroutinesApi::class)
    private var products =
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
                    dateAdded = System.currentTimeMillis()
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
            ProductEvent.SortProducts -> {
                isSortedByName.value = !isSortedByName.value
            }
        }
    }

}