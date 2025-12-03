package com.example.gesport.ui.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gesport.R
import com.example.gesport.ui.components.GeSportBackgroundScreen
import com.example.gesport.ui.components.PrimaryButton

/**
 * Pantalla de bienvenida de la aplicación.
 * Es la primera pantalla que ve el usuario y permite navegar
 * hacia el inicio de sesión o al registro.
 */
@Composable
fun WelcomeScreen(navController: NavHostController) {

    // Componente reutilizable que aplica el fondo y la capa oscura
    GeSportBackgroundScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // Sección del logo + título de la app
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    // Posición vertical del logo
                    .padding(top = 320.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    // Logo GeSport
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    // Título de la app
                    Text(
                        text = "GeSport",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 35.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Botones principales de la pantalla
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 170.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Botón para navegar al login
                PrimaryButton(
                    text = "Iniciar sesión",
                    onClick = { navController.navigate("login") }
                )

                // Botón para navegar al registro de usuario
                PrimaryButton(
                    text = "Registrarse",
                    onClick = { navController.navigate("register") }
                )
            }
        }
    }
}
