package com.example.scanner1000.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.scanner1000.data.category.CategoryEvent
import com.example.scanner1000.data.category.CategoryViewModel

@Composable
fun TextRecognitionScreen(
    navController: NavController,
    categoryViewModel: CategoryViewModel){

    val state = categoryViewModel.state.collectAsState().value

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                categoryViewModel.onEvent(CategoryEvent.SaveCategory(
                    title = state.title.value,
                ))
                navController.popBackStack()
            }) {

                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Save Category"
                )

            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = state.title.value,
                onValueChange = {
                    state.title.value = it
                },
                textStyle = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                ),
                placeholder = {
                    Text(text = "Title")
                }

            )


        }

    }

}
