package com.example.scanner1000.data.friend

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanner1000.data.Friend
import com.example.scanner1000.data.FriendDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FriendViewModel(private val dao: FriendDao) : ViewModel() {

    private val getAllFriends = MutableStateFlow(true)
    val checkedFriendsCount: Flow<Int> = dao.getCheckedFriendsCount()

    @OptIn(ExperimentalCoroutinesApi::class)
    private var friends =
        getAllFriends.flatMapLatest { dao.getAllFriends() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val _state = MutableStateFlow(FriendState())
    val state =
        combine(_state, getAllFriends, friends) { state, getAllFriends, friends ->
            state.copy(
                friends = friends
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FriendState())

    fun onEvent(event: FriendEvent)
    {
        when (event)
        {
            is FriendEvent.DeleteFriend ->
            {
                viewModelScope.launch {
                    dao.deleteFriend(event.friend)
                }
            }

            is FriendEvent.SaveFriend ->
            {
                val friend = Friend(
                    name = state.value.name.value,
                    balance = state.value.balance.value
                )

                viewModelScope.launch {
                    dao.insertFriend(friend)
                }

                _state.update {
                    it.copy(
                        name = mutableStateOf(""),
                        balance = mutableStateOf(0.0)
                    )
                }
            }

            is FriendEvent.EditFriend ->
            {
                viewModelScope.launch {
                    dao.updateFriend(event.friend) // Załóżmy, że masz taką metodę
                }

            }

            else ->
            {
            }
        }

    }

    fun setFriendChecked(friend: Friend, isChecked: Boolean) = viewModelScope.launch {
        dao.updateFriendIsChecked(friend.id, isChecked)
        // Możesz też aktualizować stan w pamięci, jeśli trzymasz tam listę produktów
    }


    fun updateFriendBalance(friendId: Int, newBalance: Double) = viewModelScope.launch {
        val friend = dao.getFriendById(friendId).firstOrNull() ?: return@launch
        val updatedFriend = friend.copy(balance = newBalance)
        dao.updateFriend(updatedFriend)
    }
}
