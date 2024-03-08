package com.example.scanner1000.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.scanner1000.data.Category
import com.example.scanner1000.data.category.CategoryEvent
import com.example.scanner1000.data.category.CategoryViewModel
import com.example.scanner1000.ui.theme.md_theme_light_onPrimaryContainer
import com.example.scanner1000.ui.theme.md_theme_light_primaryContainer

@Composable
fun ChooseCategoryScreen(
    categoryViewModel: CategoryViewModel,
    navController: NavController
) {

    val state = categoryViewModel.state.collectAsState().value
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
            bottomBar = {
                BottomAppBar(
                        actions = {
                            IconButton(onClick = { navController.navigate("mainScreen") }) {
                                Icon(
                                        imageVector = Icons.Filled.ArrowBackIosNew,
                                        contentDescription = "Powrót"
                                )
                            }
                            Spacer(Modifier.weight(1f, true))
                            Button(
                                    onClick = {
                                        if (selectedCategoryId != null) {
                                            navController.navigate("textRecognitionScreen/${selectedCategoryId}")
                                        }
                                        else {
                                            // Pokaż komunikat, że kategoria nie została wybrana
                                        }
                                    },
                            ) {
                                Text("Skanuj paragon")
                            }
                            Spacer(Modifier.weight(1f, true))

                            IconButton(onClick = { showAddDialog = true }) {
                                Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = "Dodaj kategorię"
                                )
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
                AddAlertDialog(
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
                    Text(text = "Wybierz kategorię, do której chcesz przypisać skanowane produkty.")
                }
            }
            Card(
                    modifier = Modifier
                        .fillMaxWidth()
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
                    Text(
                            text = "Kategorie",
                            fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn {
                        items(state.categories) { category ->
                            CategoryButton(
                                    category = category,
                                    isSelected = selectedCategoryId == category.id, // Sprawdzenie, czy kategoria jest wybrana
                                    onSelectCategory = {
                                        selectedCategoryId = it
                                    } // Aktualizacja wybranej kategorii
                            )
                        }
                    }
                }

            }

        }

    }
}

@Composable
fun AddAlertDialog(
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
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
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
                        label = { Text("Nazwa") },
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
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                            onClick = {
                                if (state.title.value.isBlank()) {
                                    errorMessage = "Wpisz nazwę"
                                }
                                else {
                                    onSave(state.title.value)
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
                //.padding(2.dp)
                .clickable { onSelectCategory(category.id) }
    ) {
        Text(
                text = category.title,
                modifier = Modifier.padding(16.dp),
                color = if (isSelected) md_theme_light_primaryContainer else md_theme_light_onPrimaryContainer

        )
    }
}

