package com.example.scanner1000.data.friend

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanner1000.data.Friend
import com.example.scanner1000.data.FriendDao
import com.example.scanner1000.data.Refund
import com.example.scanner1000.data.RefundDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.RoundingMode

class FriendViewModel(private val dao: FriendDao, private val refundDao: RefundDao) : ViewModel() {

    private val getAllFriends = MutableStateFlow(true)
    val checkedFriendsCount: Flow<Int> = dao.getCheckedFriendsCount()

    @OptIn(ExperimentalCoroutinesApi::class)
    var friends =
        getAllFriends.flatMapLatest { dao.getAllFriends() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val _state = MutableStateFlow(FriendState())
    val state =
        combine(_state, getAllFriends, friends) { state, getAllFriends, friends ->
            state.copy(
                friends = friends
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FriendState())

    fun onEvent(event: FriendEvent) {
        when (event) {
            is FriendEvent.DeleteFriend -> {
                viewModelScope.launch {
                    dao.deleteFriend(event.friend)
                }
            }

            is FriendEvent.SaveFriend -> {
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

            is FriendEvent.EditFriend -> {
                viewModelScope.launch {
                    dao.updateFriend(event.friend)
                }

            }

            else -> {
            }
        }

    }

    fun setFriendChecked(friend: Friend, isChecked: Boolean) = viewModelScope.launch {
        dao.updateFriendIsChecked(friend.id, isChecked)
    }

    val checkedFriendsIds: Flow<List<Int>> = dao.getCheckedFriendsIds()

    fun decreaseBalanceForCheckedFriends(amountToSubtract: Double) = viewModelScope.launch {
        dao.decreaseBalanceForCheckedFriends(amountToSubtract)
    }

    fun increaseBalanceForSpecificFriend(friendId: Int, amountToAdd: Double) =
        viewModelScope.launch {
            val friend = dao.getFriendById(friendId).first()
            val newBalance = (friend.balance + amountToAdd).toBigDecimal()
                .setScale(2, RoundingMode.HALF_EVEN).toDouble()
            dao.updateFriend(friend.copy(balance = newBalance))
        }

    fun decreaseBalanceForSpecificFriend(friendId: Int, amountToSubtract: Double) =
        viewModelScope.launch {
            val friend = dao.getFriendById(friendId).first()
            val newBalance = (friend.balance - amountToSubtract).toBigDecimal()
                .setScale(2, RoundingMode.HALF_EVEN).toDouble()
            dao.updateFriend(friend.copy(balance = newBalance))
        }

    fun getRefundsForFriend(friendId: Int): Flow<List<Refund>> {
        return refundDao.getRefundsForFriend(friendId)
    }

    fun addRefund(friendId: Int, amount: Double, description: String = "ZWROT") =
        viewModelScope.launch {
            val refund = Refund(friendId = friendId, amount = amount, description = description)
            refundDao.insertRefund(refund)
        }

    fun addExpense(friendId: Int, amount: Double, description: String) = viewModelScope.launch {
        val refund = Refund(friendId = friendId, amount = amount, description = description)
        refundDao.insertRefund(refund)
    }


    init {
        initializeDefaultFriend()
    }

    private fun initializeDefaultFriend() = viewModelScope.launch {
        val defaultFriendName = "Ja"
        val existingFriend = dao.findFriendByName(defaultFriendName)
        if (existingFriend == null) {
            val newFriend = Friend(name = defaultFriendName, balance = 0.0, isChecked = false)
            dao.insertFriend(newFriend)
        }
    }
}
