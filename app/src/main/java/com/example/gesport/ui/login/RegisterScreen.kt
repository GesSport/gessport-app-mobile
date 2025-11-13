package com.example.gesport.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gesport.R
import com.example.gesport.ui.login.components.Input
import com.example.gesport.ui.login.components.PasswordInput
import com.example.gesport.ui.login.components.PrimaryButton

@Composable
fun RegisterScreen(navController: NavHostController) {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var repeatPassword by rememberSaveable { mutableStateOf("") }


    Box(Modifier.fillMaxSize()) {
        // Fondo
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Card translúcida
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center),
            color = Color.Black.copy(alpha = 0.75f),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Logo + títulos
                Box(Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier.size(65.dp)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "GeSport",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Registro de usuario",
                            color = Color.White.copy(alpha = 0.35f),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                // Nombre de usuario
                Input(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = "Nombre de usuario",
                    leadingIconRes = R.drawable.icon_user
                )


                // Correo electrónico
                Input(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Correo electrónico",
                    leadingIconRes = R.drawable.icon_email
                )


                // Teléfono
                Input(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = "Teléfono",
                    leadingIconRes = R.drawable.icon_phone
                )


                // Contraseña
                PasswordInput(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Contraseña"
                )


                // Repetir contraseña
                PasswordInput(
                    value = repeatPassword,
                    onValueChange = { repeatPassword = it },
                    placeholder = "Repetir contraseña"
                )


                Spacer(Modifier.width(0.5.dp))

                // Botón "Enviar solicitud"
                PrimaryButton(
                    text = "Enviar solicitud",
                    onClick = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                )

                // Botón "¿Ya tienes cuenta? ... "
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("¿Ya tienes cuenta?", color = Color.White.copy(alpha = 0.65f))
                    TextButton(
                        onClick = { navController.navigate("login") },
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF2DAAE1).copy(alpha = 0.75f)
                        )
                    ) {
                        Text("Inicia sesión aquí")
                    }
                }
            }
        }
    }
}


