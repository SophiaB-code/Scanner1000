package com.example.scanner1000.ui.screens

import android.app.Activity
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.scanner1000.TextRecognizingViewModel
import com.example.scanner1000.data.Product
import com.example.scanner1000.data.category.CategoryEvent
import com.example.scanner1000.data.category.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextRecognitionScreen(
    navController: NavController,
    textRecognizingViewModel: TextRecognizingViewModel){

    val context = LocalContext.current as Activity
    val scrollState = rememberScrollState()
    val products = textRecognizingViewModel.tempRecognizedProducts.value


    val imageCropLauncher =
        rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
            if (result.isSuccessful) {
                result.uriContent?.let {

                    val newBitmap = if (Build.VERSION.SDK_INT < 28) {
                        @Suppress("DEPRECATION")
                        MediaStore.Images
                            .Media.getBitmap(context.contentResolver, it)
                    } else {
                        val source = ImageDecoder
                            .createSource(context.contentResolver, it)
                        ImageDecoder.decodeBitmap(source)
                    }
                    textRecognizingViewModel.updateBitmap(newBitmap)
                }

            } else {
                //If something went wrong you can handle the error here
                println("ImageCropping error: ${result.error}")
            }
        }

    Scaffold(
        topBar = {
                 TopAppBar(title = {Text("FairPay")},
                     actions = {
                         IconButton(
                             onClick = {
                                 val cropOptions = CropImageContractOptions(
                                     null,
                                     CropImageOptions(imageSourceIncludeCamera = false)
                                 )
                                 imageCropLauncher.launch(cropOptions)
                             }
                         ) {
                             Icon(
                                 Icons.Filled.Image,
                                 contentDescription = "Background from gallery"
                             )
                         }
                     })
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
              //  .verticalScroll(state = scrollState)
        ) {
            textRecognizingViewModel.bitmap.value?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Przycięte zdjęcie",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }

            LazyColumn {
                items(products) { product ->
                    ProductButton(product = product, onProductClick = {})

                }
            }

            Button(onClick = { textRecognizingViewModel.saveRecognizedProducts()}) {
                Text("Dodaj")

            }



        }

    }

}

@Composable
fun ProductButton(
    product: Product,
    onProductClick: (Product) -> Unit
) {
    Button(
        onClick = { onProductClick(product) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 3.dp, end = 3.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        border = BorderStroke(1.dp, Color.Black),
        shape = RectangleShape
    ) {
        Text("${product.name} | ${product.price}")
    }
}

