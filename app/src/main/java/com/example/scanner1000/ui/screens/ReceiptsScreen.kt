package com.example.scanner1000.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scanner1000.data.Category
import com.example.scanner1000.data.category.CategoryEvent
import com.example.scanner1000.data.category.CategoryViewModel

@Composable
fun ReceiptsScreen(viewModel: CategoryViewModel){
    val state = viewModel.state.collectAsState().value

    Scaffold (
        topBar = {

        }
    ){paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
                    modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)){
            items(state.categories){  category ->
                CategoryItem(
                    category = category,
                    onDeleteCategory = { viewModel.onEvent(CategoryEvent.DeleteCategory(category)) }
                )

            }
        }
    }


}

@Composable
fun CategoryItem(
    category: Category,
    onDeleteCategory: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = category.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )


        }

        IconButton(
            onClick = onDeleteCategory
        ) {

            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Delete Note",
                modifier = Modifier.size(35.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )

        }

    }
}