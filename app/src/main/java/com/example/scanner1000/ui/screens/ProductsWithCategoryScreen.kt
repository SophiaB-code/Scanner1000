package com.example.scanner1000.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.scanner1000.data.Friend
import com.example.scanner1000.data.Product
import com.example.scanner1000.data.friend.FriendEvent
import com.example.scanner1000.data.friend.FriendViewModel
import com.example.scanner1000.data.product.ProductViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsWithCategoryScreen(
    categoryId: Int,
    friendViewModel: FriendViewModel,
    productViewModel: ProductViewModel
) {
    // Przykład załadowania produktów dla danej kategorii
    LaunchedEffect(categoryId) {
        productViewModel.getProductsWithCategory(categoryId)
    }

    val products = productViewModel.productsWithCategory.collectAsState()
    var allSelected by remember { mutableStateOf(false) }
    var splitSelected by remember { mutableStateOf(false) }
    var notSplitSelected by remember { mutableStateOf(false) }
    var showSplitDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomAppBar {

                if (showSplitDialog) {
                    SplitAlertDialog(
                        onDismiss = { showSplitDialog = false },
                        friendViewModel = friendViewModel,
                    )
                }
                Button(
                    onClick = {
                        showSplitDialog = true
                    },
                    Modifier
                        .fillMaxSize()
                        .padding(5.dp)
                ) {
                    Text(text = "Podziel")
                }

            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Produkty",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                modifier = Modifier.padding(bottom = 10.dp, top = 10.dp, start = 20.dp)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,

                ) {
                item {
                    FilterChip(
                        onClick = { allSelected = !allSelected },
                        modifier = Modifier.padding(start = 10.dp),
                        label = {
                            Text("Wszystkie")
                        },
                        selected = allSelected,
                        leadingIcon = if (allSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Done icon",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        },
                    )
                }
                item {
                    FilterChip(
                        onClick = { splitSelected = !splitSelected },
                        modifier = Modifier.padding(start = 10.dp),
                        label = {
                            Text("Niepodzielone")
                        },
                        selected = splitSelected,
                        leadingIcon = if (splitSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Done icon",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        },
                    )
                }
                item {
                    FilterChip(
                        onClick = { notSplitSelected = !notSplitSelected },
                        modifier = Modifier.padding(start = 10.dp),
                        label = {
                            Text("Podzielone")
                        },
                        selected = notSplitSelected,
                        leadingIcon = if (notSplitSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Done icon",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        },
                    )
                }


            }

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(top = 12.dp, end = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products.value) { product ->
                        ProductButton(
                            product = product,
                            productViewModel
                        )


                    }
                }
            }


        }
    }
}


@Composable
fun ProductButton(
    product: Product,
    productViewModel: ProductViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable(onClick = { }),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant

        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp),

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(

                text = product.name,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                color = Color.Black,
                modifier = Modifier
                    .weight(3f)
                    .padding(8.dp),

                )

            Text(

                text = product.price.toString(),
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                color = Color.Black,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),

                )

            Checkbox(checked = product.isChecked,
                onCheckedChange = { isChecked ->
                    productViewModel.setProductChecked(product, isChecked)
                })

        }


    }

}


@Composable
fun SplitAlertDialog(
    onDismiss: () -> Unit,
    friendViewModel: FriendViewModel
) {
    val state = friendViewModel.state.collectAsState().value
    var showAddDialog by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            )
            {
                Text(
                    text = "Podziel rachunek",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn {
                    items(state.friends) { friend ->
                        FriendCheckbox(friend = friend, friendViewModel)
                    }
                }
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
                Row {
                    TextButton(onClick = { showAddDialog = true }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "")
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "dodaj znajomego")
                    }
                }

                Row {
                    TextButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Anuluj")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {

                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Zapisz")
                    }
                }
            }
        }
    }
}

@Composable
fun FriendCheckbox(friend: Friend, friendViewModel: FriendViewModel) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = friend.isChecked,
            onCheckedChange = { isChecked ->
                friendViewModel.setFriendChecked(friend, isChecked)
            })
        Text(text = friend.name)
    }
}

@Composable
fun AddFriendDialog(
    onDismiss: () -> Unit,
    friendViewModel: FriendViewModel,
    onSave: (String) -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            )
            {
                val state = friendViewModel.state.collectAsState().value
                var errorMessage by remember { mutableStateOf<String?>(null) }

                Text(
                    text = "Dodaj znajomego",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    value = state.name.value,
                    onValueChange = {
                        state.name.value = it
                    },
                    textStyle = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp
                    ),
                    label = { Text("Imię") },
                    isError = errorMessage != null
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    TextButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Anuluj")
                    }
                    Spacer(modifier = Modifier.width(80.dp))
                    TextButton(
                        onClick = {
                            if (state.name.value.isBlank()) {
                                errorMessage = "Pole imię nie może być puste"
                            } else {
                                onSave(state.name.value)
                            }
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Dodaj")
                    }
                }
            }
        }
    }
}