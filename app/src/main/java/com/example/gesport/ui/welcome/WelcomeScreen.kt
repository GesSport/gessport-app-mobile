package com.example.gesport.ui.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gesport.R
import com.example.gesport.ui.components.PrimaryButton

@Composable
fun WelcomeScreen(navController: NavHostController) {

    Box(Modifier.fillMaxSize()) {
        // Fondo
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Capa oscura
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.70f),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                // Logo + Título
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 320.dp),        // <-- mueve el logo hacia abajo
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp)
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "GeSport",
                            color = Color.White,
                            fontSize = 35.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Contenido principal
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 170.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    // Botón "Iniciar sesión"
                    PrimaryButton(
                        text = "Iniciar sesión",
                        onClick = {navController.navigate("login")}
                    )

                    // Botón "Iniciar sesión con Google"
                    PrimaryButton(
                        text = "Registrarse",
                        onClick = {navController.navigate("register")}
                    )

                }
            }
        }
    }
}
