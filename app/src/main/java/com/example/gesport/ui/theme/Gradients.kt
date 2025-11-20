package com.example.gesport.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

//  Gradiente principal de la app
val AppGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF6BDD96), // verde claro
        Color(0xFF14B3A2), // turquesa
        Color(0xFF1E8794)  // azul verdoso
    ),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, 0f) // 90 grados (horizontal)
)