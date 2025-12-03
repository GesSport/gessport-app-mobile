package com.example.gesport.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Pantalla de inicio para usuarios.
 *
 */
@Composable
fun HomeScreen(navController: NavController, name: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Mensaje de bienvenida con el nombre recibido desde la navegación
        Text(
            "Bienvenido/a, $name",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(24.dp))

        // Botón para cerrar sesión y volver a la pantalla de bienvenida
        Button(
            onClick = {
                navController.navigate("welcome") {
                    // Elimina todo el historial de pantallas para que no se pueda volver atrás
                    popUpTo(0)
                    launchSingleTop = true
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF257DA4),
                contentColor = Color.White
            )
        ) {
            Text("Cerrar sesión")
        }
    }
}
