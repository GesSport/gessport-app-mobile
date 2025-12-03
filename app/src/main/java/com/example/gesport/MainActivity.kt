package com.example.gesport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.example.gesport.navigation.Navigation

/**
 * Actividad principal de la aplicación.
 * Es el punto de entrada donde se inicializa Jetpack Compose.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                // Lanza la navegación principal
                Navigation()
            }
        }
    }
}
