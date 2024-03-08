package com.example.scanner1000.ui.screens

import android.app.Activity
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.scanner1000.TextRecognizingViewModel

@Composable
fun ChoosePhotoScreen(
    textRecognizingViewModel: TextRecognizingViewModel,
    navController: NavController,
    selectedCategoryId: Int?
)
{
    val context = LocalContext.current as Activity


    val imageCropLauncher =
        rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
            if (result.isSuccessful)
            {
                result.uriContent?.let {

                    val newBitmap = if (Build.VERSION.SDK_INT < 28)
                    {
                        @Suppress("DEPRECATION")
                        MediaStore.Images
                                .Media.getBitmap(context.contentResolver, it)
                    }
                    else
                    {
                        val source = ImageDecoder
                                .createSource(context.contentResolver, it)
                        ImageDecoder.decodeBitmap(source)
                    }
                    textRecognizingViewModel.updateBitmap(newBitmap)
                    navController.navigate("textRecognitionScreen/${selectedCategoryId}")
                }

            }
            else
            {
                //If something went wrong you can handle the error here
                println("ImageCropping error: ${result.error}")
            }
        }

    Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.Center
    ) {
        Text(
                text = "Dodaj zdjęcie",
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp, bottom = 10.dp, start = 10.dp, end = 10.dp),
                fontSize = 30.sp,
                textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(15.dp))
        LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GalleryAndCameraButton(
                        label = "Galeria",
                        imageVector = Icons.Filled.Photo,
                        onClick = {
                            val cropOptions = CropImageContractOptions(
                                    null,
                                    CropImageOptions(imageSourceIncludeCamera = false)
                            )
                            imageCropLauncher.launch(cropOptions)
                        },
                        contentDescription = "Wybierz zdjęcie z galerii",
                        colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        )
                )
            }
            item {
                GalleryAndCameraButton(
                        label = "Aparat",
                        imageVector = Icons.Filled.CameraAlt,
                        onClick = {
                            // Implementacja uruchomienia aparatu i przycięcia zdjęcia będzie tutaj
                        },
                        contentDescription = "Zrób zdjęcie aparatem",
                        colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        )
                )
            }
        }

    }
}


@Composable
fun GalleryAndCameraButton(
    label: String,
    imageVector: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    colors: CardColors
)
{
    Card(
            modifier = Modifier
                    .clickable(onClick = onClick)
                    .size(160.dp),
            shape = RoundedCornerShape(20.dp),
            colors = colors
    ) {
        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                        imageVector = imageVector,
                        contentDescription = contentDescription,
                        modifier = Modifier
                                .size(60.dp)
                                .align(Alignment.Top),
                        tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                    text = label,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    textAlign = TextAlign.Start,
                    color = Color.Black
            )
        }

    }
}

