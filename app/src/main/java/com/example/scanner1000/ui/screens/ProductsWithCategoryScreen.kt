package com.example.scanner1000.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LibraryAddCheck
import androidx.compose.material.icons.outlined.LibraryAddCheck
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.scanner1000.data.Friend
import com.example.scanner1000.data.Product
import com.example.scanner1000.data.category.CategoryViewModel
import com.example.scanner1000.data.friend.FriendEvent
import com.example.scanner1000.data.friend.FriendViewModel
import com.example.scanner1000.data.product.ProductEvent
import com.example.scanner1000.data.product.ProductViewModel
import com.example.scanner1000.ui.theme.Rubik
import java.math.RoundingMode


@Composable
fun ProductsWithCategoryScreen(
    categoryId: Int,
    friendViewModel: FriendViewModel,
    productViewModel: ProductViewModel,
    categoryViewModel: CategoryViewModel
) {
    // Przykład załadowania produktów dla danej kategorii
    LaunchedEffect(categoryId) {
        productViewModel.getProductsWithCategory(categoryId)
    }

    val products = productViewModel.productsWithCategory.collectAsState().value
    var filteredProducts = products
    var allSelected by remember { mutableStateOf(false) }
    var splitSelected by remember { mutableStateOf(false) }
    var notSplitSelected by remember { mutableStateOf(true) }
    var showSplitDialog by remember { mutableStateOf(false) }
    var isIconFilled by remember { mutableStateOf(false) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    val categoryName =
        categoryViewModel.getCategoryTitleById(categoryId).collectAsState(initial = "")


    filteredProducts = when {
        splitSelected -> products.filter { it.isSplit }
        notSplitSelected -> products.filter { !it.isSplit }
        else -> products
    }

    Scaffold(
        bottomBar = {
            if (notSplitSelected) {
                BottomAppBar {

                    if (showSplitDialog) {
                        SplitAlertDialog(
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
                    text = categoryName.value,
                    modifier = Modifier
                        .padding(bottom = 10.dp, top = 10.dp, start = 20.dp)
                        .weight(3f),
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    style = TextStyle(
                        fontFamily = Rubik,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium
                    ),
                )
                if (notSplitSelected) {
                    IconButton(
                        onClick = { showAddProductDialog = true },
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

                    IconButton(
                        onClick = {
                            isIconFilled = !isIconFilled
                            if (isIconFilled) {
                                productViewModel.setNotSplitProductsChecked(true) // Zakładając, że ta metoda zaznacza produkty
                            } else {
                                productViewModel.setNotSplitProductsChecked(false) // Zakładając, że ta metoda odznacza produkty
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 5.dp)
                    ) {
                        Icon(
                            imageVector = if (isIconFilled) Icons.Filled.LibraryAddCheck else Icons.Outlined.LibraryAddCheck,
                            contentDescription = "",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                if (showAddProductDialog) {
                    AddProductDialog(
                        onDismiss = { showAddProductDialog = false },
                        productViewModel = productViewModel,
                        onSave = { name, price ->
                            productViewModel.addProduct(
                                Product(
                                    name = name,
                                    price = price,
                                    categoryFk = categoryId,
                                    isSplit = false,
                                    isChecked = false,
                                    dateAdded = System.currentTimeMillis()
                                )
                            )
                        }
                    )
                }
                if (allSelected) {

                    IconButton(
                        onClick = { showAddProductDialog = true },
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
                        }
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
                        }
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
                                notSplitSelected = false,
                                onDeleteProduct = {
                                    productViewModel.onEvent(
                                        ProductEvent.DeleteProduct(
                                            product
                                        )
                                    )
                                }
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
                                notSplitSelected = true,
                                onDeleteProduct = {
                                    productViewModel.onEvent(
                                        ProductEvent.DeleteProduct(
                                            product
                                        )
                                    )
                                }
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
                                notSplitSelected = false,
                                onDeleteProduct = {
                                    productViewModel.onEvent(
                                        ProductEvent.DeleteProduct(
                                            product
                                        )
                                    )
                                }
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
    notSplitSelected: Boolean,
    onDeleteProduct: () -> Unit,
) {
    var showAlertDialog by remember { mutableStateOf(false) }
    var showProductEditDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    if (showAlertDialog) {
        DeleteAlertDialog(
            message = "Czy na pewno chcesz usunąć produkt?",
            onDismissRequest = { showAlertDialog = false },
            onConfirmation = {
                onDeleteProduct()
                showAlertDialog = false
            }
        )
    }
    if (showProductEditDialog) {
        EditProductDialog(
            onDismiss = { showProductEditDialog = false },
            productViewModel = productViewModel,
            onEditProduct = { updatedProduct ->
                productViewModel.onEvent(ProductEvent.EditProduct(updatedProduct))
            },
            product = product
        )
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable(onClick = { showProductEditDialog = true }),
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
            Row(
                modifier = Modifier.weight(8f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = product.name,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    color = Color.Black,
                    modifier = Modifier
                        .weight(3f)
                        .padding(8.dp),
                    style = TextStyle(
                        fontFamily = Rubik,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Light
                    )
                )
                Text(
                    text = " ${product.price} zł",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    color = Color.Black,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    style = TextStyle(
                        fontFamily = Rubik,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Light
                    ),
                    textAlign = TextAlign.End
                )
            }
            Box(
                modifier = Modifier.weight(1f)
            ) {

                if (notSplitSelected) {
                    Checkbox(checked = product.isChecked,
                        onCheckedChange = { isChecked ->
                            productViewModel.setProductChecked(product, isChecked)
                        })
                }
                if (allSelected || splitSelected) {
                    IconButton(onClick = { expanded = true }) {
                        Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (product.isSplit) {
                            DropdownMenuItem(
                                text = { Text("Cofnij podział") },
                                onClick = {
                                    expanded = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Usuń") },
                            onClick = {
                                expanded = false
                                showAlertDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SplitAlertDialog(
    onDismiss: () -> Unit,
    friendViewModel: FriendViewModel,
    productViewModel: ProductViewModel
) {

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
    val checkedProductIds by productViewModel.checkedProductIds.collectAsState(initial = emptyList())
    val selectedFriendIds =
        friendViewModel.checkedFriendsIds.collectAsState(initial = emptyList()).value

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
                        FriendCheckbox(friend = friend, friendViewModel, amountPerFriend)
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
                                productViewModel.resetProductsCheckedStatus()

                                checkedProductIds.forEach { productId ->
                                    selectedFriendIds.forEach { friendId ->
                                        if (amountPerFriend != null) {
                                            productViewModel.addSharedProductInfo(
                                                productId,
                                                friendId,
                                                amountPerFriend
                                            )
                                        }
                                    }
                                }

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
fun FriendCheckbox(
    friend: Friend,
    friendViewModel: FriendViewModel,
    amountPerFriend: Double?
) {

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

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    productViewModel: ProductViewModel,
    onSave: (String, Double) -> Unit
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
                var productName by remember { mutableStateOf("") }
                // Stan dla ceny produktu
                var productPrice by remember { mutableStateOf("") }

                val state = productViewModel.state.collectAsState().value
                var errorMessage by remember { mutableStateOf<String?>(null) }

                Text(
                    text = "Dodaj produkt",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    style = TextStyle(
                        fontFamily = Rubik,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 4.dp, top = 4.dp),
                    value = productName,
                    onValueChange = {
                        productName = it
                    },
                    textStyle =
                    TextStyle(
                        fontFamily = Rubik,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Light

                    ),
                    label = {
                        Text(
                            "Nazwa",
                            style = TextStyle(
                                fontFamily = Rubik,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Light
                            ),
                        )
                    },
                    singleLine = true,
                    isError = errorMessage != null
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 4.dp, top = 4.dp),
                    value = productPrice,
                    onValueChange = { newValue ->
                        // Wyrażenie regularne pasujące do liczby z maksymalnie dwoma miejscami po przecinku
                        val pattern = Regex("^\\d*\\.?\\d{0,2}$")

                        // Sprawdzamy, czy nowa wartość pasuje do wzorca
                        if (newValue.matches(pattern) || newValue.isEmpty()) {
                            productPrice = newValue
                        }

                    },
                    textStyle =
                    TextStyle(
                        fontFamily = Rubik,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Light

                    ),

                    label = {
                        Text(
                            "Cena",
                            style = TextStyle(
                                fontFamily = Rubik,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Light
                            ),
                        )
                    },
                    singleLine = true,
                    isError = errorMessage != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.height(10.dp))
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    TextButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Anuluj",
                            style = TextStyle(
                                fontFamily = Rubik,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Light
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            val productNameCropped =
                                productName.trim() // Usuwamy białe znaki z początku i końca


                            if (productNameCropped.isBlank() || productPrice.isBlank()) {
                                errorMessage = "Wypełnij wszystkie pola"
                            } else {
                                onSave(productName, productPrice.toDouble())

                                onDismiss()
                            }

                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Zapisz",
                            style = TextStyle(
                                fontFamily = Rubik,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Light
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditProductDialog(
    onDismiss: () -> Unit,
    productViewModel: ProductViewModel,
    onEditProduct: (Product) -> Unit,
    product: Product
) {
    var editingName by remember { mutableStateOf(product.name) }
    var editingPrice by remember { mutableStateOf(product.price.toString()) }

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
                val state = productViewModel.state.collectAsState().value
                var errorMessage by remember { mutableStateOf<String?>(null) }

                Text(
                    text = "Edytuj produkt",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = Rubik,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal
                    ),
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    value = editingName,
                    onValueChange = {
                        editingName = it
                    },
                    textStyle = TextStyle(
                        fontFamily = Rubik,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Light
                    ),
                    label = {
                        Text(
                            "Nazwa",
                            style = TextStyle(
                                fontFamily = Rubik,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Light
                            ),
                        )
                    },
                    isError = errorMessage != null
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    value = editingPrice,
                    onValueChange = { newValue ->
                        // Wyrażenie regularne pasujące do liczby z maksymalnie dwoma miejscami po przecinku
                        val pattern = Regex("^\\d*\\.?\\d{0,2}$")

                        // Sprawdzamy, czy nowa wartość pasuje do wzorca
                        if (newValue.matches(pattern) || newValue.isEmpty()) {
                            editingPrice = newValue
                        }
                    },
                    textStyle = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp
                    ),
                    label = {
                        Text(
                            "Cena",
                            style = TextStyle(
                                fontFamily = Rubik,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Light
                            ),
                        )
                    },
                    singleLine = true,
                    isError = errorMessage != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
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
                        Text(
                            "Anuluj",
                            style = TextStyle(
                                fontFamily = Rubik,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Light
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(80.dp))
                    Button(
                        onClick = {
                            if (editingName.isBlank() || editingPrice.isBlank()) {
                                errorMessage = "Uzupełnij pola"
                            } else {
                                onEditProduct(
                                    product.copy(
                                        name = editingName,
                                        price = editingPrice.toDouble()
                                    )
                                )
                            }
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Dodaj",
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

