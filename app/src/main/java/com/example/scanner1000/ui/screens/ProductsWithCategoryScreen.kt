package com.example.scanner1000.ui.screens

import android.util.Log
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
import androidx.compose.material.icons.outlined.LibraryAddCheck
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.scanner1000.data.Friend
import com.example.scanner1000.data.Product
import com.example.scanner1000.data.friend.FriendEvent
import com.example.scanner1000.data.friend.FriendViewModel
import com.example.scanner1000.data.product.ProductViewModel
import com.example.scanner1000.ui.theme.Rubik
import java.math.RoundingMode



@Composable
fun ProductsWithCategoryScreen(
    productId: Int,
    categoryId: Int,
    friendViewModel: FriendViewModel,
    productViewModel: ProductViewModel
) {
    // Przykład załadowania produktów dla danej kategorii
    LaunchedEffect(categoryId) {
        productViewModel.getProductsWithCategory(categoryId)
    }

    val products = productViewModel.productsWithCategory.collectAsState().value

    var filteredProducts = products

    var allSelected by remember { mutableStateOf(false) }
    var splitSelected by remember { mutableStateOf(true) }
    var notSplitSelected by remember { mutableStateOf(false) }
    var showSplitDialog by remember { mutableStateOf(false) }

    filteredProducts = when {
        splitSelected -> products.filter { !it.isSplit }
        notSplitSelected -> products.filter { it.isSplit }
        else -> products
    }

    Scaffold(
        bottomBar = {
            if (notSplitSelected) {
                BottomAppBar {

                    if (showSplitDialog) {
                        SplitAlertDialog(
                            productId = productId,
                            onDismiss = { showSplitDialog = false },
                            friendViewModel = friendViewModel,
                            productViewModel
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = "Produkty",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontFamily = Rubik,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(bottom = 10.dp, top = 10.dp, start = 20.dp).weight(3f)
                )
                if (notSplitSelected) {
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.weight(1f).padding(start = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LibraryAddCheck,
                            contentDescription = "",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                if (allSelected) {

                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,

                ) {
                item {
                    FilterChip(
                        onClick = {
                            allSelected = true
                            splitSelected = false
                            notSplitSelected = false
                        },
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
                        onClick = {
                            allSelected = false
                            splitSelected = false
                            notSplitSelected = true
                        },
                        modifier = Modifier.padding(start = 10.dp),
                        label = {
                            Text("Niepodzielone")
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
                item {
                    FilterChip(
                        onClick = {
                            allSelected = false
                            splitSelected = true
                            notSplitSelected = false
                        },
                        modifier = Modifier.padding(start = 10.dp),
                        label = {
                            Text("Podzielone")
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


            }

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            ) {
                if (allSelected) {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 12.dp, end = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredProducts) { product ->
                            ProductButton(
                                product = product,
                                productViewModel,
                                allSelected = true,
                                splitSelected = false,
                                notSplitSelected = false
                            )


                        }
                    }
                }
                if (notSplitSelected) {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 12.dp, end = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredProducts) { product ->
                            ProductButton(
                                product = product,
                                productViewModel,
                                allSelected = false,
                                splitSelected = false,
                                notSplitSelected = true
                            )


                        }
                    }
                }
                if (splitSelected) {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 12.dp, end = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredProducts) { product ->
                            ProductButton(
                                product = product,
                                productViewModel,
                                allSelected = false,
                                splitSelected = true,
                                notSplitSelected = false
                            )


                        }
                    }
                }

            }


        }
    }
    DisposableEffect(Unit) {
        onDispose {
            productViewModel.resetProductsCheckedStatus()
        }
    }
}


@Composable
fun ProductButton(
    product: Product,
    productViewModel: ProductViewModel,
    allSelected: Boolean,
    splitSelected: Boolean,
    notSplitSelected: Boolean
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
            if (notSplitSelected) {
                Checkbox(checked = product.isChecked,
                    onCheckedChange = { isChecked ->
                        productViewModel.setProductChecked(product, isChecked)
                    })
            }
            if (allSelected || splitSelected) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "")
                }
            }

        }


    }

}


@Composable
fun SplitAlertDialog(
    productId: Int,
    onDismiss: () -> Unit,
    friendViewModel: FriendViewModel,
    productViewModel: ProductViewModel
) {

    val selectedFriendIds = friendViewModel.checkedFriendsIds.value
    val state = friendViewModel.state.collectAsState().value
    var showAddDialog by remember { mutableStateOf(false) }
    val sumOfCheckedProducts by productViewModel.sumOfCheckedProducts.collectAsState(initial = null)
    val checkedFriendsCount by friendViewModel.checkedFriendsCount.collectAsState(initial = 0)
    val amountPerFriend =
        if (checkedFriendsCount > 0 && sumOfCheckedProducts != null) {
            (sumOfCheckedProducts?.div(checkedFriendsCount))?.toBigDecimal()
                ?.setScale(2, RoundingMode.HALF_EVEN)?.toDouble()
        } else {
            null
        }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
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
                        FriendCheckbox(friend = friend, friendViewModel,amountPerFriend)
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

                            if (checkedFriendsCount > 0 && sumOfCheckedProducts != null) {
                                if (amountPerFriend != null) {
                                    friendViewModel.decreaseBalanceForCheckedFriends(amountPerFriend)
                                }
                                productViewModel.updateProductsAsSplit()

                                Log.d(
                                    "YourScreen",
                                    "Każdy znajomy powinien zapłacić: $amountPerFriend"
                                )


                                onDismiss()

                            } else {
                                Log.d("YourScreen", "Nie wybrano żadnych znajomych")
                            }

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
fun FriendCheckbox(friend: Friend, friendViewModel: FriendViewModel,amountPerFriend: Double?) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(checked = friend.isChecked,
            onCheckedChange = { isChecked ->
                friendViewModel.setFriendChecked(friend, isChecked)

            })
        Text(text = friend.name)
        Spacer(Modifier.weight(1f))
        if (amountPerFriend != null) {
            // Wyświetlanie kwoty obok nazwy znajomego
            Text(text = " - ${amountPerFriend} zł")
        }
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

