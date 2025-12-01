package com.example.gesport.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.gesport.ui.components.Input
import com.example.gesport.ui.components.PasswordInput
import com.example.gesport.ui.components.PrimaryButton

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

        // Capa oscura
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.70f),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 🔹 TITULO + LOGO + SUBTÍTULO (ANTES DENTRO DEL HEADER)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp) // misma altura que el login
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart),
                        horizontalAlignment = Alignment.Start
                    ) {



                        Spacer(Modifier.height(20.dp))

                        // TÍTULO DEBAJO DEL NOMBRE
                        Text(
                            text = "Registro de usuario",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Texto explicativo
                Text(
                    text = "Completa el formulario con tus datos personales para crear una nueva cuenta en GeSport. Asegúrate de que la información introducida sea correcta antes de enviar la solicitud.",
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                )


                Spacer(Modifier.height(1.dp))

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

                Spacer(Modifier.height(4.dp))

                // Botón "Enviar solicitud"
                PrimaryButton(
                    text = "Enviar solicitud",
                    onClick = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                )

                // Botón "¿Ya tienes cuenta? ..."
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
