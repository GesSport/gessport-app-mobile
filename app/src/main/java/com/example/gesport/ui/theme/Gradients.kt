package com.example.gesport.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Define los gradientes que podemos usar en la aplicación.
 *
 */
val AppGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF6BDD96), // Verde claro
        Color(0xFF14B3A2), // Turquesa
        Color(0xFF1E8794)  // Azul verdoso
    ),
    start = Offset(0f, 0f),                         // inicio del degradado (izquierda)
    end = Offset(Float.POSITIVE_INFINITY, 0f)       // fin del degradado (derecha)
)
