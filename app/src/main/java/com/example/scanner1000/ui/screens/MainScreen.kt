package com.example.scanner1000.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController){

    Column {

        Button(
            onClick = { navController.navigate("textRecognitionScreen") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)

        ) {
            Text("Dodaj paragon")
        }

        Button(
            onClick = { navController.navigate("receiptsScreen") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)

        ) {
            Text("Moje paragony")
        }


        Button(
            onClick = { navController.navigate("receiptsScreen") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)

        ) {
            Text("Moje paragony")
        }
    }

}