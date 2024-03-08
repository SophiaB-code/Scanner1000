package com.example.scanner1000.ui.screens

import android.app.Activity
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.scanner1000.TextRecognizingViewModel
import com.example.scanner1000.data.Product
import com.example.scanner1000.ui.theme.md_theme_light_background
import com.example.scanner1000.ui.theme.md_theme_light_primary
import com.example.scanner1000.ui.theme.md_theme_light_surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextRecognitionScreen(
    navController: NavController,
    textRecognizingViewModel: TextRecognizingViewModel,
    selectedCategoryId: Int?
    ){

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
        },
        bottomBar = {
            if (products.isNotEmpty()) {
            BottomAppBar (modifier = Modifier.height(IntrinsicSize.Min),
                          containerColor = md_theme_light_background) {
                    Button(
                            modifier = Modifier
                                    .padding(3.dp)
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min),
                            onClick = {
                                if (selectedCategoryId != null) {
                                    textRecognizingViewModel.saveRecognizedProducts(
                                            selectedCategoryId
                                    )
                                }
                                else {
                                    // Obsługa przypadku, gdy kategoria nie została wybrana (opcjonalnie)
                                }
                            }) {
                        Text("Dodaj")
                    }
                }
            }
        }
    ) { paddingValues ->

        Column(
                modifier = Modifier
                        .padding(paddingValues)
                        .verticalScroll(state = scrollState)
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
            Column {
                products.forEach { product ->
                    ProductButton(product = product, onProductClick = {})
                }
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
            containerColor = md_theme_light_surface,
            contentColor = md_theme_light_primary
        ),
        border = BorderStroke(1.dp, md_theme_light_primary),
        shape = RectangleShape
    ) {
        Text("${product.name} | ${product.price}")
    }
}


