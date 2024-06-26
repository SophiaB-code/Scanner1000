package com.example.scanner1000.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.scanner1000.data.Friend
import com.example.scanner1000.data.Product
import com.example.scanner1000.data.category.CategoryViewModel
import com.example.scanner1000.data.friend.FriendEvent
import com.example.scanner1000.data.friend.FriendViewModel
import com.example.scanner1000.data.product.ProductViewModel
import com.example.scanner1000.ui.screens.FriendCheckbox
import com.example.scanner1000.ui.theme.md_theme_light_secondary
import java.math.RoundingMode

@Composable
fun SplitDialog(
    onDismiss: () -> Unit,
    friendViewModel: FriendViewModel,
    productViewModel: ProductViewModel
) {

    val state = friendViewModel.state.collectAsState().value
    var showAddDialog by remember { mutableStateOf(false) }
    val sumOfCheckedProducts by productViewModel.sumOfCheckedProducts.collectAsState(initial = null)
    val priceOfCheckedProduct by productViewModel.priceOfCheckedProduct.collectAsState(initial = null)
    val checkedFriendsCount by friendViewModel.checkedFriendsCount.collectAsState(initial = 0)
    val amountPerFriend =
        if (checkedFriendsCount > 0 && sumOfCheckedProducts != null) {
            (sumOfCheckedProducts?.div(checkedFriendsCount))?.toBigDecimal()
                ?.setScale(2, RoundingMode.HALF_EVEN)?.toDouble()
        } else {
            null
        }
    val amountPerFriendPerProduct =
        if (checkedFriendsCount > 0) {
            (priceOfCheckedProduct?.div(checkedFriendsCount))?.toBigDecimal()
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
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
                        Text(text = "dodaj znajomego", style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Row {
                    TextButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Anuluj", style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {

                            if (checkedFriendsCount > 0 && sumOfCheckedProducts != null) {
                                if (amountPerFriend != null) {
                                    friendViewModel.decreaseBalanceForCheckedFriends(amountPerFriend)
                                }
                                productViewModel.updateProductsAsSplit()
                                productViewModel.resetProductsCheckedStatus()

                                checkedProductIds.forEach { productId ->
                                    selectedFriendIds.forEach { friendId ->
                                        if (amountPerFriendPerProduct != null) {
                                            productViewModel.addSharedProductInfo(
                                                productId,
                                                friendId,
                                                amountPerFriendPerProduct
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
                        Text("Zapisz", style = MaterialTheme.typography.bodyLarge)
                    }

                }

            }
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
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
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
                    textStyle = MaterialTheme.typography.bodyLarge,
                    label = { Text("Imię", style = MaterialTheme.typography.bodyLarge) },
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
                        Text("Anuluj", style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(modifier = Modifier.width(80.dp))
                    Button(
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
                        Text("Dodaj", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
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
                var productPrice by remember { mutableStateOf("") }
                var errorMessage by remember { mutableStateOf<String?>(null) }

                Text(
                    text = "Dodaj produkt",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    style = MaterialTheme.typography.titleMedium,
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
                    textStyle = MaterialTheme.typography.bodyLarge,
                    label = {
                        Text(
                            "Nazwa",
                            style = MaterialTheme.typography.bodyLarge,
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
                    textStyle = MaterialTheme.typography.bodyLarge,
                    label = {
                        Text(
                            "Cena",
                            style = MaterialTheme.typography.bodyLarge,
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
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
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
                            style = MaterialTheme.typography.bodyLarge,
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
                var errorMessage by remember { mutableStateOf<String?>(null) }

                Text(
                    text = "Edytuj produkt",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    textAlign = TextAlign.Center
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
                    textStyle = MaterialTheme.typography.bodyLarge,
                    label = {
                        Text(
                            "Nazwa",
                            style = MaterialTheme.typography.bodyLarge
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
                    textStyle = MaterialTheme.typography.bodyLarge,
                    label = {
                        Text(
                            "Cena",
                            style = MaterialTheme.typography.bodyLarge
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
                            style = MaterialTheme.typography.bodyLarge
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
                            "Zapisz",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    categoryViewModel: CategoryViewModel,
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
                val state = categoryViewModel.state.collectAsState().value
                var errorMessage by remember { mutableStateOf<String?>(null) }

                Text(
                    text = "Dodaj kategorię",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    style = MaterialTheme.typography.titleMedium,
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
                    value = state.title.value,
                    onValueChange = {
                        state.title.value = it
                    },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    label = {
                        Text(
                            "Nazwa",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    singleLine = true,
                    isError = errorMessage != null
                )
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
                        Text("Anuluj", style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val categoryName =
                                state.title.value.trim() // Usuwamy białe znaki z początku i końca
                            if (categoryName.isBlank()) {
                                errorMessage = "Wpisz nazwę"
                            } else {
                                // Sprawdzamy, czy lista kategorii zawiera już kategorię o tej samej nazwie
                                val categoryExists =
                                    categoryViewModel.state.value.categories.any {
                                        it.title.equals(
                                            categoryName,
                                            ignoreCase = true
                                        )
                                    }
                                if (categoryExists) {
                                    errorMessage = "Kategoria o tej nazwie już istnieje"
                                } else {
                                    onSave(categoryName)
                                    onDismiss()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Zapisz", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun BalanceEditDialog(
    friend: Friend,
    onDismiss: () -> Unit,
    friendViewModel: FriendViewModel,
    onSave: (String, Double) -> Unit
) {
    var isRefund by remember { mutableStateOf(true) }
    var productName by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = friend.name,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        style = MaterialTheme.typography.titleMedium

                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { isRefund = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRefund) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Zwrot", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { isRefund = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isRefund) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Wydatek", style = MaterialTheme.typography.titleMedium)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (!isRefund) {
                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        label = {
                            Text(
                                "Nazwa",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        singleLine = true,
                        isError = errorMessage != null,
                        textStyle = MaterialTheme.typography.bodyLarge,
                    )
                }
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = productPrice,
                    onValueChange = { newValue ->
                        // Wyrażenie regularne pasujące do liczby z maksymalnie dwoma miejscami po przecinku
                        val pattern = Regex("^\\d*\\.?\\d{0,2}$")

                        // Sprawdzamy, czy nowa wartość pasuje do wzorca
                        if (newValue.matches(pattern) || newValue.isEmpty()) {
                            productPrice = newValue
                        }
                    },
                    singleLine = true,
                    isError = errorMessage != null,
                    label = {
                        Text(
                            "Kwota",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { onDismiss() }
                    ) {
                        Text(
                            "Anuluj",
                            style = MaterialTheme.typography.bodyLarge,
                            color = md_theme_light_secondary
                        )
                    }
                    Button(
                        onClick = {
                            val productNameCropped =
                                productName.trim()

                            val amountValue = productPrice.toDoubleOrNull()
                            if (amountValue != null) {
                                if (isRefund) {
                                    friendViewModel.increaseBalanceForSpecificFriend(
                                        friend.id,
                                        amountValue
                                    )
                                    friendViewModel.addRefund(friend.id, amountValue, "ZWROT")

                                } else {
                                    if (productNameCropped.isBlank() || productPrice.isBlank()) {
                                        errorMessage = "Wypełnij wszystkie pola"
                                    } else {
                                        onSave(productNameCropped, amountValue)
                                    }
                                    friendViewModel.decreaseBalanceForSpecificFriend(
                                        friend.id,
                                        amountValue
                                    )
                                    friendViewModel.addExpense(
                                        friend.id,
                                        amountValue,
                                        productNameCropped
                                    )
                                }
                            } else {
                                errorMessage = "Podaj kwotę"
                            }
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(md_theme_light_secondary)
                    )
                    {
                        Text("Zapisz", style = MaterialTheme.typography.bodyLarge)
                    }
                }

            }
        }
    }
}
