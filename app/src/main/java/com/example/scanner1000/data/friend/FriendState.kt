package com.example.scanner1000.data.friend

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.scanner1000.data.Friend

data class FriendState(

    val friends: List<Friend> = emptyList(),
    val name: MutableState<String> = mutableStateOf(""),
    val balance: MutableState<Double> = mutableStateOf(00.00),
    val isChecked: MutableState<Boolean> = mutableStateOf(false)

    )