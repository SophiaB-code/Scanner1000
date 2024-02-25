package com.example.scanner1000.data.category


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanner1000.data.Category
import com.example.scanner1000.data.CategoryDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class CategoryViewModel(
    private val dao: CategoryDao
) : ViewModel() {

    private val isSortedByDateAdded = MutableStateFlow(true)

    @OptIn(ExperimentalCoroutinesApi::class)
    private var categories =
        isSortedByDateAdded.flatMapLatest {dao.getCategoryOrderedByDateAdded() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val _state = MutableStateFlow(CategoryState())
    val state =
        combine(_state, isSortedByDateAdded, categories) { state, isSortedByDateAdded, categories ->
            state.copy(
                categories = categories
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoryState())

    fun onEvent(event: CategoryEvent) {
        when (event) {
            is CategoryEvent.DeleteCategory -> {
                viewModelScope.launch {
                    dao.deleteCategory(event.category)
                }
            }

            is CategoryEvent.SaveCategory -> {
                val category = Category(
                    title = state.value.title.value,
                    dateAdded = System.currentTimeMillis()
                )

                viewModelScope.launch {
                    dao.upsertCategory(category)
                }

                _state.update {
                    it.copy(
                        title = mutableStateOf(""),
                    )
                }
            }

            CategoryEvent.SortCategory -> {
                isSortedByDateAdded.value = !isSortedByDateAdded.value
            }
        }
    }

}