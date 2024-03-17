package com.example.scanner1000.data.friend

import com.example.scanner1000.data.Category
import com.example.scanner1000.data.Friend
import com.example.scanner1000.data.category.CategoryEvent

sealed interface FriendEvent {
    object SortFriend: FriendEvent

    data class DeleteFriend(val friend: Friend): FriendEvent

    data class SaveFriend(
        val name: String,
        val balance: Double
    ): FriendEvent

    data class EditFriend(val friend: Friend): FriendEvent
}
