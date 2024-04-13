package com.example.scanner1000

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.scanner1000.data.AppDatabase
import com.example.scanner1000.data.Friend
import com.example.scanner1000.data.FriendDao
import com.example.scanner1000.data.Refund
import com.example.scanner1000.data.RefundDao
import com.example.scanner1000.data.friend.FriendEvent
import com.example.scanner1000.data.friend.FriendState
import com.example.scanner1000.data.friend.FriendViewModel
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FriendViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var friendDao: FriendDao
    private lateinit var refundDao: RefundDao
    private lateinit var viewModel: FriendViewModel
    private lateinit var database: AppDatabase

    @Before
    fun setupDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()

        friendDao = database.friendDao()
        refundDao = database.refundDao()
        viewModel = FriendViewModel(friendDao, refundDao)
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun setFriendCheckedShouldUpdateFriendsIsCheckedStatusCorrectly() = runTest {
        val friend = Friend("John", 50.0, false, 1)
        friendDao.insertFriend(friend)
        val isChecked = true
        viewModel.setFriendChecked(friend, isChecked)
        TestCase.assertEquals(isChecked, friendDao.getFriendById(1).first().isChecked)
    }

    @Test
    fun decreaseBalanceForCheckedFriends_ShouldSubtractAmountFromCheckedFriends() = runTest {
        val friend1 = Friend("John", 50.0, true, 1)
        val friend2 = Friend("Alice", 30.0, false, 2)
        friendDao.insertFriend(friend1)
        friendDao.insertFriend(friend2)
        val amountToSubtract = 20.0
        viewModel.decreaseBalanceForCheckedFriends(amountToSubtract)
        val updatedFriend1 = friendDao.getFriendById(1).first()
        val updatedFriend2 = friendDao.getFriendById(2).first()
        TestCase.assertEquals(30.0, updatedFriend1.balance)
        TestCase.assertEquals(30.0, updatedFriend2.balance)
    }

    @Test
    fun increaseBalanceForSpecificFriend_ShouldAddAmountToFriendBalance() = runTest {
        val friend = Friend("John", 50.0, false, 1)
        friendDao.insertFriend(friend)
        val amountToAdd = 30.0
        viewModel.increaseBalanceForSpecificFriend(1, amountToAdd)
        println(friendDao.getAllFriends().first().size)
        val updatedFriend = friendDao.getFriendById(1).first()
        TestCase.assertEquals(80.0, updatedFriend.balance)
    }

    @Test
    fun decreaseBalanceForSpecificFriend_ShouldSubtractAmountFromFriendBalance() = runTest {
        val friend = Friend("John", 50.0, false, 1)
        friendDao.insertFriend(friend)
        val amountToSubtract = 20.0
        viewModel.decreaseBalanceForSpecificFriend(friend.id, amountToSubtract)
        println(friendDao.getAllFriends().first().size)
        val updatedFriend = friendDao.getFriendById(friend.id).first()
        TestCase.assertEquals(30.0, updatedFriend.balance)
    }

    @Test
    fun getRefundsForFriend_ShouldReturnListOfRefundsForSpecifiedFriend() = runTest {
        val friend = Friend("John", 50.0, false, 1)
        friendDao.insertFriend(friend)
        val refund1 = Refund(friend.id, 20.0, "Refund1")
        val refund2 = Refund(friend.id, 15.0, "Refund2")
        refundDao.insertRefund(refund1)
        refundDao.insertRefund(refund2)
        println(friendDao.getAllFriends().first().size)
        val refunds = viewModel.getRefundsForFriend(friend.id).first()
        TestCase.assertTrue(refunds.isNotEmpty())
    }

    @Test
    fun addRefund_ShouldInsertRefundIntoDatabase() = runTest {
        val friendId = 1
        val amount = 20.0
        val description = "Test refund"
        viewModel.addRefund(friendId, amount, description)
        val refunds = refundDao.getRefundsForFriend(friendId).first()
        assertFalse(refunds.isEmpty())
        assertEquals(friendId, refunds[0].friendId)
        assertEquals(amount, refunds[0].amount)
        assertEquals(description, refunds[0].description)
    }

    @Test
    fun addExpense_ShouldInsertExpenseIntoDatabase() = runTest {
        val friendId = 2
        val amount = 50.0
        val description = "Test expense"
        viewModel.addExpense(friendId, amount, description)
        val refunds = refundDao.getRefundsForFriend(friendId).first()
        assertFalse(refunds.isEmpty())
        assertEquals(friendId, refunds[0].friendId)
        assertEquals(amount, refunds[0].amount)
        assertEquals(description, refunds[0].description)
    }

    @Test
    fun onEvent_DeleteFriend_ShouldDeleteFriendFromDatabase() = runTest {
        val friendToDelete = Friend("John", 50.0, false, 1)
        friendDao.insertFriend(friendToDelete)
        viewModel.onEvent(FriendEvent.DeleteFriend(friendToDelete))
        val deletedFriend = friendDao.getFriendById(1).first()
        assertNull(deletedFriend)
    }

    @Test
    fun onEvent_EditFriend_ShouldUpdateFriendInDatabase() = runTest {
        val friendToUpdate = Friend("John", 50.0, false, 1)
        friendDao.insertFriend(friendToUpdate)
        viewModel.onEvent(FriendEvent.EditFriend(friendToUpdate))
        val updatedFriend = friendDao.getFriendById(1).first()
        assertNotNull(updatedFriend)
    }
}