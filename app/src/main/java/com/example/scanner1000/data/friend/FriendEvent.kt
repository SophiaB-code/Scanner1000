package com.example.scanner1000.data.friend

import com.example.scanner1000.data.Friend

sealed interface FriendEvent {
    object SortFriend: FriendEvent

    data class DeleteFriend(val friend: Friend): FriendEvent

    data class SaveFriend(
        val name: String,
        val balance: Double,
        val isChecked: Boolean
    ): FriendEvent

    data class EditFriend(val friend: Friend): FriendEvent
}
