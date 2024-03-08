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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController){

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to FairPay!",
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(top = 25.dp, bottom = 10.dp, start = 10.dp, end = 10.dp),
             fontSize = 30.sp,
             textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(15.dp))
        LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CardButton(
                        imageVector = Icons.Filled.PostAdd,
                        contentDescription = "dodaj paragon",
                        label = "Dodaj nowy paragon",
                        onClick = { navController.navigate("chooseCategoryScreen") },
                        colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                )
            }
            item {
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
            item {
                CardButton(
                        imageVector = Icons.Filled.PeopleAlt,
                        contentDescription = "znajomi",
                        label = "Moi znajomi",
                        onClick = { /* Definiujesz akcję */ },
                        colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        )
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
                .size(width = 180.dp, height = 180.dp),
            shape = RoundedCornerShape(20.dp),
            colors = colors
    ) {
        Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
            ) {
                Icon(
                        imageVector = imageVector,
                        contentDescription = contentDescription,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Top),
                        tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    text = label,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    textAlign = TextAlign.Start,
                    color = Color.Black
            )
        }

    }
}