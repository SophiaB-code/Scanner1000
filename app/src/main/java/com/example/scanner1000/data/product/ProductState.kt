package com.example.scanner1000.data.product

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.scanner1000.data.Product

data class ProductState(

    val products: List<Product> = emptyList(),
    val name: MutableState<String> = mutableStateOf(""),
    val price: MutableState<Double> = mutableStateOf(00.00),
    val isSplit: MutableState<Int> = mutableStateOf(0),
    val isChecked: MutableState<Boolean> = mutableStateOf(false)

)