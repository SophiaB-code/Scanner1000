package com.example.scanner1000.data.category

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.scanner1000.data.Category

data class CategoryState(

    val categories: List<Category> = emptyList(),
    val title: MutableState<String> = mutableStateOf(""),

    )