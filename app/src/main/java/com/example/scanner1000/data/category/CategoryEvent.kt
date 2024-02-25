package com.example.scanner1000.data.category

import com.example.scanner1000.data.Category

sealed interface CategoryEvent {
    object SortCategory: CategoryEvent

    data class DeleteCategory(val category: Category): CategoryEvent

    data class SaveCategory(
        val title: String
    ): CategoryEvent
}
