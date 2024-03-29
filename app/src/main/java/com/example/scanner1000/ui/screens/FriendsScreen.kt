package com.example.scanner1000.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.scanner1000.data.Friend
import com.example.scanner1000.data.friend.FriendEvent
import com.example.scanner1000.data.friend.FriendViewModel
import com.example.scanner1000.data.product.ProductViewModel
import com.example.scanner1000.ui.components.AddFriendDialog
import com.example.scanner1000.ui.theme.Rubik
import com.example.scanner1000.ui.theme.md_theme_light_onPrimaryContainer
import com.example.scanner1000.ui.theme.md_theme_light_tertiaryContainer

@Composable
fun FriendsScreen(
    friendViewModel: FriendViewModel,
    productViewModel: ProductViewModel
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    val state = friendViewModel.state.collectAsState().value
    val filteredFriends = state.friends.filterNot { it.name == "Ja" }

    if (showAddDialog) {
        AddFriendDialog(
            onDismiss = { showAddDialog = false },
            friendViewModel = friendViewModel,
            onSave = { name ->
                friendViewModel.onEvent(
                    FriendEvent.SaveFriend(
                        name = name,
                        balance = 0.0,
                        isChecked = false
                    )
                )
            }
        )
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Text(
                text = "Znajomi",
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                style = TextStyle(
                    fontFamily = Rubik,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .weight(4f)
                    .padding(top = 10.dp, start = 8.dp),
                textAlign = TextAlign.Start
            )
            if (!isDeleting) {
                IconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Dodaj znajomego",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            IconButton(
                onClick = { isDeleting = !isDeleting },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isDeleting) Icons.Filled.Done else Icons.Rounded.Delete,
                    contentDescription = "Usuń znajomego",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(15.dp))

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (filteredFriends.isEmpty()) {
                item {
                    Text(
                        text = "Brak znajomych",
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontFamily = Rubik,
                            fontSize = 20.sp,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            } else {
                items(filteredFriends) { friend ->
                    FriendButton(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        ),
                        friend = friend,
                        onEditFriend = { updatedFriend ->
                            friendViewModel.onEvent(FriendEvent.EditFriend(updatedFriend))
                        },
                        onDeleteFriend = {
                            friendViewModel.onEvent(
                                FriendEvent.DeleteFriend(
                                    friend
                                )
                            )
                        },
                        isDeleting = isDeleting,
                        productViewModel = productViewModel,
                        friendViewModel = friendViewModel,
                        friendId = friend.id
                    )

                }
            }
        }
    }
}

@Composable
fun FriendButton(
    colors: CardColors,
    onEditFriend: (Friend) -> Unit,
    onDeleteFriend: () -> Unit,
    friend: Friend,
    isDeleting: Boolean,
    productViewModel: ProductViewModel,
    friendViewModel: FriendViewModel,
    friendId: Int
) {
    var showAlertDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var editingName by remember { mutableStateOf(friend.name) }
    var isExpanded by remember { mutableStateOf(false) } // Stan kontrolujący, czy rozszerzyć dodatkowy Card
    val productInfoForFriend by productViewModel.getProductInfoForFriend(friendId)
        .collectAsState(initial = emptyList())
    val refundsForFriend by friendViewModel.getRefundsForFriend(friendId)
        .collectAsState(initial = emptyList())

    if (showAlertDialog) {
        DeleteAlertDialog(
            message = "Czy na pewno chcesz usunąć znajomego?",
            onDismissRequest = { showAlertDialog = false },
            onConfirmation = {
                onDeleteFriend()
                showAlertDialog = false
            }
        )
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isEditing) 80.dp else 60.dp)
            .clickable(enabled = !isDeleting, onClick = {
                if (!isEditing) {
                    isExpanded = !isExpanded
                }
            }),
        shape = if (isExpanded) {
            RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            )
        } else {
            RoundedCornerShape(20.dp)
        },
        colors = colors
    ) {
        Column {

        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (!isEditing) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = friend.name,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(start = 8.dp),
                        style = TextStyle(
                            fontFamily = Rubik,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Light
                        ),
                    )
                    if (!isDeleting) {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edytuj nazwę znajomego",
                                tint = Color.Black,
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .size(25.dp)
                            )
                        }
                    }
                }

                if (isDeleting) {
                    IconButton(onClick = {
                        showAlertDialog = true

                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Usuń znajomego",
                            tint = Color.Black,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .size(35.dp)
                        )
                    }
                } else {

                    Text(
                        text = friend.balance.toString() + " zł",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(end = 22.dp),
                        style = TextStyle(
                            fontFamily = Rubik,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Light
                        )
                    )
                }
            } else {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxHeight()
                        .padding(bottom = 8.dp),
                    label = { },
                    value = editingName,
                    onValueChange = { editingName = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontFamily = Rubik,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Light
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = md_theme_light_tertiaryContainer,
                        unfocusedContainerColor = md_theme_light_tertiaryContainer,
                        disabledContainerColor = md_theme_light_tertiaryContainer,
                    ),
                    shape = RoundedCornerShape(20.dp)


                )
                IconButton(onClick = {
                    isEditing = false
                    onEditFriend(friend.copy(name = editingName))
                }) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Zapisz zamiany",
                        tint = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }


    }
    if (isExpanded) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, top = 0.dp),
            shape = RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp)
        ) {
            Column {
                productInfoForFriend.forEach { info ->

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 10.dp, top = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = info.name,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(8.dp),
                            style = TextStyle(
                                fontFamily = Rubik,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Light
                            )
                        )


                        Text(
                            text = "- ${info.amountPerFriend} zł",
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(end = 22.dp),
                            style = TextStyle(
                                fontFamily = Rubik,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Light
                            )
                        )

                    }
                }
                refundsForFriend.forEach { refund ->

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 10.dp, top = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = refund.description,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(8.dp),
                            style = TextStyle(
                                fontFamily = Rubik,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Light
                            )
                        )


                        Text(
                            text = "+ ${refund.amount} zł",
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(end = 22.dp),
                            style = TextStyle(
                                fontFamily = Rubik,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Light
                            )
                        )

                    }

                }
            }
        }
    }
}

@Composable
fun DeleteAlertDialog(
    message: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .width(300.dp)
                .height(200.dp),
            //.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "",
                    tint = md_theme_light_onPrimaryContainer
                )
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center

                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Anuluj")
                    }
                    Button(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Usuń")
                    }
                }
            }
        }
    }
}


