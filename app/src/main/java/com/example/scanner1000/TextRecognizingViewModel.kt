package com.example.scanner1000

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanner1000.data.AppDatabase
import com.example.scanner1000.data.Product
import com.example.scanner1000.data.ProductDao
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch

class TextRecognizingViewModel (application: Application) : ViewModel() {
    private val productDao: ProductDao = AppDatabase.getDatabase(application).productDao()
    var bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    private var recognizedText by mutableStateOf("")


    fun updateBitmapAndRecognizeText(newBitmap: Bitmap, selectedCategoryId: Int) {
        bitmap.value = newBitmap
        recognizeTextFromImage(newBitmap, selectedCategoryId) // Teraz przekazujemy ID kategorii
    }


    private fun recognizeTextFromImage(bitmap: Bitmap,  selectedCategoryId: Int) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val lines = visionText.text.split("\n")
                val products = mutableListOf<Product>()

                val array = lines.subList(0, lines.size / 2)
                val array2 = lines.subList(lines.size / 2, lines.size)

                val prices= mutableListOf<String>()
                array2.forEach {
                    it.trim()
                    val priceMatches =  Regex("\\d+[,.]\\d{2}").findAll(it)
                    it.replace(",",".")
                    val price = priceMatches.lastOrNull()?.value?.replace(",", ".") ?: "0.00"
                    prices.add(price)
                }



                for (index in array.indices) {
                    val price = prices[index].toDoubleOrNull() ?: 0.0 // Przekształcenie stringa na Double
                    val product = Product(
                        name = array[index],
                        price = price,
                        dateAdded = System.currentTimeMillis(),
                        categoryFk = selectedCategoryId,
                        isSplit = false
                    )
                    viewModelScope.launch {
                        productDao.upsertProduct(product) // Zapisujemy każdy produkt bezpośrednio do bazy danych
                    }
                }

                recognizedText = array.zip(prices) { name, price ->
                    "$name | $price"
                }.joinToString(separator = "\n")
            }
            .addOnFailureListener { e ->
                recognizedText = "Nie udało się rozpoznać tekstu: ${e.localizedMessage}"
            }

    }
}