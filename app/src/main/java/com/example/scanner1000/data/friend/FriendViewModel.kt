package com.example.scanner1000.data.friend

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.scanner1000.data.Friend
import com.example.scanner1000.data.FriendDao
import kotlinx.coroutines.launch

class FriendViewModel(private val friendDao: FriendDao) : ViewModel() {


    val allFriends: LiveData<List<Friend>> = friendDao.getAllFriends().asLiveData()


    fun insertFriend(friend: Friend) = viewModelScope.launch {
        friendDao.insertFriend(friend)
    }


    fun updateFriend(friend: Friend) = viewModelScope.launch {
        friendDao.updateFriend(friend)
    }


    fun deleteFriend(friend: Friend) = viewModelScope.launch {
        friendDao.deleteFriend(friend)
    }


    fun getFriendById(friendId: Int): LiveData<Friend> = friendDao.getFriendById(friendId).asLiveData()
}
