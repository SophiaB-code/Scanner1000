package com.example.scanner1000

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.scanner1000.data.AppDatabase
import com.example.scanner1000.data.Product
import com.example.scanner1000.data.ProductDao
import com.example.scanner1000.data.product.ProductViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch

class TextRecognizingViewModel (application: Application) : ViewModel() {
    private val productDao: ProductDao = AppDatabase.getDatabase(application).productDao()
    private val allProducts: LiveData<List<Product>> = productDao.getProductsOrderedByName().asLiveData()
    var bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    private var recognizedText by mutableStateOf("")
//    var products = mutableStateOf(listOf<Product>())
//        private set
    fun addProduct(product: Product) = viewModelScope.launch {
      productDao.upsertProduct(product)
    }

    fun deleteProduct(product: Product) = viewModelScope.launch {
        productDao.deleteProduct(product)
    }

    fun getProductsOrderedByName(): LiveData<List<Product>> = allProducts

    fun updateBitmap(newBitmap: Bitmap) {
        bitmap.value = newBitmap
        recognizeTextFromImage(newBitmap, this)
    }


    private fun recognizeTextFromImage(bitmap: Bitmap, textRecognizingViewModel: TextRecognizingViewModel) {
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
                        dateAdded = System.currentTimeMillis()
                        // Usunięto id, Room zajmie się generowaniem ID
                    )
                    viewModelScope.launch {
                        productDao.upsertProduct(product)
                    }
                }


                recognizedText = products.joinToString(separator = "\n") { product ->
                    "${product.name} | ${product.price}"

                }
                //ProductViewModel.updateProducts(products)
            }
            .addOnFailureListener { e ->
                recognizedText = "Nie udało się rozpoznać tekstu: ${e.localizedMessage}"
            }

    }
}