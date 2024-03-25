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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.RoundingMode

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


    private val _checkedFriends = MutableStateFlow<List<Friend>>(emptyList())
    val checkedFriends: StateFlow<List<Friend>> = _checkedFriends
    fun getCheckedFriends() {
        viewModelScope.launch {
            dao.getCheckedFriends().collect { friends ->
                _checkedFriends.value = friends
            }
        }
    }
    private val _checkedFriendsIds = MutableStateFlow<List<Int>>(emptyList())
    val checkedFriendsIds: StateFlow<List<Int>> = _checkedFriendsIds

    init {
        getCheckedFriendsIds()
    }
    fun getCheckedFriendsIds() {
        viewModelScope.launch {
            dao.getCheckedFriendsIds().collect { ids ->
                _checkedFriendsIds.value = ids
            }
        }
    }

    fun updateFriendBalance(friend: Friend, amount: Double) = viewModelScope.launch {
        val updatedFriend = friend.copy(balance = friend.balance + amount)
        dao.updateFriend(updatedFriend)
    }


    fun decreaseBalanceForCheckedFriends(amountToSubtract: Double) = viewModelScope.launch {
        dao.decreaseBalanceForCheckedFriends(amountToSubtract)
    }

    fun increaseBalanceForSpecificFriend(friendId: Int, amountToAdd: Double) = viewModelScope.launch {
        val friend = dao.getFriendById(friendId).first()
        val newBalance = (friend.balance + amountToAdd).toBigDecimal()
            .setScale(2, RoundingMode.HALF_EVEN).toDouble()
        dao.updateFriend(friend.copy(balance = newBalance))
    }

    fun decreaseBalanceForSpecificFriend(friendId: Int, amountToSubtract: Double) = viewModelScope.launch {
        val friend = dao.getFriendById(friendId).first()
        val newBalance = (friend.balance - amountToSubtract).toBigDecimal()
            .setScale(2, RoundingMode.HALF_EVEN).toDouble()
        dao.updateFriend(friend.copy(balance = newBalance))
    }


}
