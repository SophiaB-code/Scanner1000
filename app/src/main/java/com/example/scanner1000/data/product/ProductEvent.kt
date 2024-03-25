package com.example.scanner1000.data.product

import com.example.scanner1000.data.Product

sealed interface ProductEvent {
    object SortProducts: ProductEvent

    data class DeleteProduct(val product: Product): ProductEvent

    data class SaveProduct(
        val name: String,
        val price: Double,
        val categoryFk: Int,
        val isSplit: Boolean,
        val isChecked: Boolean
    ): ProductEvent

    data class EditProduct(val product: Product): ProductEvent

    data class UpdateProduct(
        val isChecked: Boolean
    ):ProductEvent
}
