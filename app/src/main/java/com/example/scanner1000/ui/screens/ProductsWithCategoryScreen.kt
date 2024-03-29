package com.example.scanner1000.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.scanner1000.data.Friend
import com.example.scanner1000.data.Product
import com.example.scanner1000.data.category.CategoryViewModel
import com.example.scanner1000.data.friend.FriendViewModel
import com.example.scanner1000.data.product.ProductEvent
import com.example.scanner1000.data.product.ProductViewModel
import com.example.scanner1000.ui.components.AddProductDialog
import com.example.scanner1000.ui.components.EditProductDialog
import com.example.scanner1000.ui.components.SplitDialog
import com.example.scanner1000.ui.theme.Rubik


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
                        SplitDialog(
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
                                    productViewModel.undoProductSplit(product)
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


