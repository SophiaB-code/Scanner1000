package com.example.scanner1000.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.AddBox
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.scanner1000.data.Category
import com.example.scanner1000.data.category.CategoryEvent
import com.example.scanner1000.data.category.CategoryViewModel
import com.example.scanner1000.ui.theme.Rubik
import com.example.scanner1000.ui.theme.md_theme_light_onPrimaryContainer
import com.example.scanner1000.ui.theme.md_theme_light_primaryContainer
import my.nanihadesuka.compose.LazyColumnScrollbar

@Composable
fun ChooseCategoryScreen(
    categoryViewModel: CategoryViewModel,
    navController: NavController
) {

    val state = categoryViewModel.state.collectAsState().value
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            BottomAppBar(
                actions = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        IconButton(
                            onClick = { navController.navigate("mainScreen") }, modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Powrót",
                                modifier = Modifier.size(45.dp)
                            )
                        }
                        Button(
                            onClick = {
                                if (selectedCategoryId != null) {
                                    navController.navigate("choosePhotoScreen/${selectedCategoryId}")
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Nie wybrano kategorii",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxHeight()
                                .align(Alignment.Center)
                        ) {
                            Text(
                                "Skanuj paragon",
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                style = TextStyle(
                                    fontFamily = Rubik,
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (showAddDialog) {
                AddCategoryDialog(
                    onDismiss = { showAddDialog = false },
                    categoryViewModel = categoryViewModel,
                    onSave = { title ->
                        categoryViewModel.onEvent(CategoryEvent.SaveCategory(title = title))
                        showAddDialog = false
                    })
            }
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.Lightbulb, contentDescription = "tutorial")
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Wybierz kategorię, do której chcesz przypisać skanowane produkty.",
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        style = TextStyle(
                            fontFamily = Rubik,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Light
                        )
                    )
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Row {
                        Text(
                            text = "Kategorie",
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                            style = TextStyle(
                                fontFamily = Rubik,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier
                                .weight(5f)
                                .padding(10.dp)
                        )

                        IconButton(
                            onClick = { showAddDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddBox,
                                contentDescription = "Dodaj kategorię",
                                modifier = Modifier.size(40.dp)
                            )
                        }


                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val filteredCategories = state.categories.filterNot { it.id == 1 }

                    if (filteredCategories.isEmpty()) {
                        Text(text = "Brak kategorii", Modifier.padding(5.dp))
                    } else {

                        val listState = rememberLazyListState()
                        LazyColumnScrollbar(
                            listState,
                            rightSide = true,
                            alwaysShowScrollBar = true,
                            thumbColor = md_theme_light_onPrimaryContainer,
                            thumbSelectedColor = md_theme_light_onPrimaryContainer,
                            thickness = 4.dp,
                            padding = 3.dp
                        ) {
                            LazyColumn(state = listState) {
                                items(filteredCategories) { category ->
                                    CategoryButton(
                                        category = category,
                                        isSelected = selectedCategoryId == category.id,
                                        onSelectCategory = {
                                            selectedCategoryId = it
                                        }
                                    )
                                }
                            }
                        }
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
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    style = TextStyle(
                        fontFamily = Rubik,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
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
                    textStyle = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp
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
                        Text("Anuluj")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            val categoryName =
                                state.title.value.trim() // Usuwamy białe znaki z początku i końca
                            if (categoryName.isBlank()) {
                                errorMessage = "Wpisz nazwę"
                            } else {
                                // Sprawdzamy, czy lista kategorii zawiera już kategorię o tej samej nazwie
                                val categoryExists = categoryViewModel.state.value.categories.any {
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
                        Text("Zapisz")
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryButton(category: Category, isSelected: Boolean, onSelectCategory: (Int) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) md_theme_light_onPrimaryContainer else md_theme_light_primaryContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectCategory(category.id) }
            .padding(end = 12.dp)
    ) {
        Text(
            text = category.title,
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            modifier = Modifier.padding(16.dp),
            color = if (isSelected) md_theme_light_primaryContainer else md_theme_light_onPrimaryContainer,
            style = TextStyle(
                fontFamily = Rubik,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Light
            )

        )
    }
}


