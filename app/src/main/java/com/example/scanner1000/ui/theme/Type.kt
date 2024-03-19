package com.example.scanner1000.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.scanner1000.R


val Rubik = FontFamily(

    Font(R.font.rubik_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.rubik_medium_italic, FontWeight.Medium, FontStyle.Italic),

    Font(R.font.rubik_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.rubik_italic, FontWeight.Normal, FontStyle.Italic),

    Font(R.font.rubik_light, FontWeight.Light, FontStyle.Normal),
    Font(R.font.rubik_light_italic, FontWeight.Light, FontStyle.Italic),


)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)