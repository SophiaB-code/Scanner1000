package com.example.scanner1000.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.scanner1000.data.Category
import com.example.scanner1000.data.category.CategoryEvent
import com.example.scanner1000.data.category.CategoryViewModel
import com.example.scanner1000.data.product.ProductViewModel
import com.example.scanner1000.ui.components.AddCategoryDialog
import com.example.scanner1000.ui.theme.Rubik
import com.example.scanner1000.ui.theme.md_theme_light_secondaryContainer

@Composable
fun ReceiptsScreen(
    categoryViewModel: CategoryViewModel,
    productViewModel: ProductViewModel,
    navController: NavController
) {
    BackHandler {
        // Nawigacja do ReceiptsScreen
        navController.navigate("mainScreen") {
            // Usuwa wszystkie instancje `receiptsScreen` z stosu i nawiguje do niego
            popUpTo("mainScreen") { inclusive = true }
            launchSingleTop = true // Uruchamia `ReceiptsScreen` jako pojedynczy top, unikając wielokrotnych instancji
        }
    }
    val state = categoryViewModel.state.collectAsState().value
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddCategoryDialog(
            onDismiss = { showAddDialog = false },
            categoryViewModel = categoryViewModel,
            onSave = { title ->
                categoryViewModel.onEvent(CategoryEvent.SaveCategory(title = title))
                showAddDialog = false
            })
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (modifier = Modifier.fillMaxWidth().padding(15.dp)){
            Text(
                text = "Paragony",
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                style = TextStyle(
                    fontFamily = Rubik,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .weight(4f)
                    .padding(top = 10.dp, start = 8.dp),
                textAlign = TextAlign.Start
            )
            IconButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Dodaj kategorię",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            val filteredCategories = state.categories.filterNot { it.id == 1 }

            items(filteredCategories) { category ->
                CategoryButton(
                    viewModel = productViewModel,
                    contentDescription = "Kategoria",
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    category = category,
                    onDeleteCategory = {
                        categoryViewModel.onEvent(
                            CategoryEvent.DeleteCategory(
                                category
                            )
                        )
                    },
                    onEditCategory = { updatedCategory ->
                        categoryViewModel.onEvent(CategoryEvent.EditCategory(updatedCategory))
                    },
                    navController = navController
                )

            }
        }
    }
}


@Composable
fun CategoryButton(
    viewModel: ProductViewModel,
    contentDescription: String,
    colors: CardColors,
    category: Category,
    onDeleteCategory: () -> Unit,
    onEditCategory: (Category) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var editingTitle by remember { mutableStateOf(category.title) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isEditing) 80.dp else 60.dp)
            .clickable(onClick = {
                if (!isEditing) {
                    viewModel.getProductsWithCategory(category.id)
                    navController.navigate("productsWithCategoryScreen/${category.id}")
                }
            }),
        shape = RoundedCornerShape(20.dp),
        colors = colors
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            if (!isEditing) {
                Text(
                    text = category.title,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    style = TextStyle(
                        fontFamily = Rubik,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Light
                    ),
                    color = Color.Black,
                    modifier = Modifier
                        .weight(3f)
                        .padding(8.dp)
                )
            } else {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxHeight()
                        .padding(bottom = 8.dp),
                    label = { },
                    value = editingTitle,
                    onValueChange = { editingTitle = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontFamily = Rubik,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Light
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = md_theme_light_secondaryContainer,
                        unfocusedContainerColor = md_theme_light_secondaryContainer,
                        disabledContainerColor = md_theme_light_secondaryContainer,
                    ),
                    shape = RoundedCornerShape(20.dp)


                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 25.dp)
            ) {

                if (!isEditing) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = contentDescription,
                            tint = Color.Black
                        )
                    }
                } else {
                    IconButton(onClick = {
                        isEditing = false
                        onEditCategory(category.copy(title = editingTitle))
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = contentDescription,
                            tint = Color.Black
                        )
                    }
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edytuj") },
                        onClick = {
                            expanded = false
                            isEditing = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Usuń") },
                        onClick = {
                            Toast.makeText(context, "Usunięto kategorię", Toast.LENGTH_SHORT)
                                .show()
                            expanded = false
                            onDeleteCategory()
                        }
                    )

                }
            }

        }


    }

}

