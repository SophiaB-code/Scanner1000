package com.example.scanner1000.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.scanner1000.data.Friend
import com.example.scanner1000.data.Product
import com.example.scanner1000.data.friend.FriendViewModel
import com.example.scanner1000.data.product.ProductViewModel
import com.example.scanner1000.ui.components.BalanceEditDialog
import com.example.scanner1000.ui.theme.Rubik

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    friendViewModel: FriendViewModel,
    productViewModel: ProductViewModel,
    navController: NavController
) {

    val state = friendViewModel.state.collectAsState().value
    val filteredFriends = state.friends.filterNot { it.name == "Ja" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "FairPay",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp, bottom = 10.dp, start = 10.dp, end = 10.dp),
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = Rubik,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(15.dp))
                Column(Modifier.padding(5.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CardButton(
                            imageVector = Icons.Filled.PostAdd,
                            contentDescription = "dodaj paragon",
                            label = "Dodaj nowy paragon",
                            onClick = { navController.navigate("chooseCategoryScreen") },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                            )
                        )
                        CardButton(
                            imageVector = Icons.Filled.Receipt,
                            contentDescription = "moje paragony",
                            label = "Moje paragony",
                            onClick = { navController.navigate("receiptsScreen") },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        CardButton(
                            imageVector = Icons.Filled.PeopleAlt,
                            contentDescription = "znajomi",
                            label = "Moi znajomi",
                            onClick = { navController.navigate("friendsScreen") },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            )
                        )
                    }
                }
            }
        }
        stickyHeader {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Icon(
                        imageVector = Icons.Rounded.AttachMoney,
                        contentDescription = "money",
                        modifier = Modifier
                            .padding(12.dp)
                            .size(30.dp),
                        tint = Color.Black
                    )
                    Text(
                        text = "BILANS WYDATKÓW",
                        modifier = Modifier.padding(12.dp),
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = TextStyle(
                            fontFamily = Rubik,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Normal
                        )
                    )
                    Icon(
                        imageVector = Icons.Rounded.AttachMoney,
                        contentDescription = "money",
                        modifier = Modifier
                            .padding(12.dp)
                            .size(30.dp),
                        tint = Color.Black
                    )
                }

            }
        }

        if (filteredFriends.isEmpty()) {
            item {
                Text(
                    text = "Brak danych",
                    modifier = Modifier
                        .padding(5.dp)
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
                FriendBalanceCard(
                    friend = friend,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    friendViewModel,
                    productViewModel
                )
            }
        }

    }

}


@Composable
fun CardButton(
    label: String,
    imageVector: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    colors: CardColors
) {
    Card(
        modifier = Modifier
            .clickable(onClick = onClick)
            .size(width = 180.dp, height = 100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = colors
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = label,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    modifier = Modifier.align(Alignment.TopStart),
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = Rubik,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal
                    )
                )
                Icon(
                    imageVector = imageVector,
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(35.dp),
                    tint = Color.Black
                )
            }
        }

    }
}

@Composable
fun FriendBalanceCard(
    friend: Friend,
    colors: CardColors,
    friendViewModel: FriendViewModel,
    productViewModel: ProductViewModel
) {
    val friends by friendViewModel.friends.collectAsState()
    var showBalanceDialog by remember { mutableStateOf(false) }
    if (showBalanceDialog) {
        BalanceEditDialog(
            friend,
            onDismiss = { showBalanceDialog = false },
            friendViewModel = friendViewModel,
            onSave = { name, price ->
                productViewModel.addProduct(
                    Product(
                        name = name,
                        price = price,
                        categoryFk = 1,
                        isSplit = false,
                        isChecked = false,
                        dateAdded = System.currentTimeMillis()
                    )
                )
            }
        )
    }

    Column {
        Card(
            modifier = Modifier
                .clickable { showBalanceDialog = true }
                .fillMaxWidth()
                .height(70.dp),
            colors = colors,
            shape = RectangleShape
        ) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (friends.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Brak znajomych",
                            style = TextStyle(
                                fontFamily = Rubik,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Medium
                            ),
                            fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        )
                    }
                } else {
                    Text(
                        text = friend.name,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = Color.Black,
                        style = TextStyle(
                            fontFamily = Rubik,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Medium
                        )

                    )


                    Text(
                        text = friend.balance.toString() + " zł",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = Color.Black,
                        style = TextStyle(
                            fontFamily = Rubik,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}



