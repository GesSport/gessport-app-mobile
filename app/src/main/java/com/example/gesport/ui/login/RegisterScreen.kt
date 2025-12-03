package com.example.gesport.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gesport.R
import com.example.gesport.domain.LoginLogic
import com.example.gesport.ui.components.GeSportBackgroundScreen
import com.example.gesport.ui.components.Input
import com.example.gesport.ui.components.PasswordInput
import com.example.gesport.ui.components.PrimaryButton

/**
 * Pantalla de registro de usuario.
 *
 * Muestra un formulario con validación de campos para crear una nueva cuenta
 * (nombre, email, teléfono y contraseña).
 *
 * Las validaciones de negocio se delegan en la clase LoginLogic.
 */
@Composable
fun RegisterScreen(navController: NavHostController) {
    // Instancia de la lógica de validación
    val loginLogic = remember { LoginLogic() }

    // Estado de los campos del formulario
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var repeatPassword by rememberSaveable { mutableStateOf("") }

    // Errores por campo (si son null, no se muestra error)
    var usernameError by rememberSaveable { mutableStateOf<String?>(null) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var phoneError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var repeatPasswordError by rememberSaveable { mutableStateOf<String?>(null) }

    Box(Modifier.fillMaxSize()) {
        // Componente reutilizable que aplica el fondo y la capa oscura
        GeSportBackgroundScreen {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Contenedor Título + Texto explicativo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart),
                        horizontalAlignment = Alignment.Start
                    ) {

                        Spacer(Modifier.height(20.dp))

                        // Título principal
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

                // Contenedor scrolleable(formulario)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // Input Nombre de usuario
                        Input(
                            value = username,
                            onValueChange = {
                                username = it
                                usernameError = null // limpia el error al modificar el campo
                            },
                            placeholder = "Nombre de usuario",
                            leadingIconRes = R.drawable.icon_user
                        )
                        usernameError?.let {
                            Text(
                                text = it,
                                color = Color(0xFFFF6B6B),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        // Input Correo electrónico
                        Input(
                            value = email,
                            onValueChange = {
                                email = it
                                emailError = null
                            },
                            placeholder = "Correo electrónico",
                            leadingIconRes = R.drawable.icon_email
                        )
                        emailError?.let {
                            Text(
                                text = it,
                                color = Color(0xFFFF6B6B),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        // Input Teléfono
                        Input(
                            value = phone,
                            onValueChange = {
                                phone = it
                                phoneError = null
                            },
                            placeholder = "Teléfono",
                            leadingIconRes = R.drawable.icon_phone
                        )
                        phoneError?.let {
                            Text(
                                text = it,
                                color = Color(0xFFFF6B6B),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        // Input Contraseña
                        PasswordInput(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordError = null
                            },
                            placeholder = "Contraseña"
                        )
                        passwordError?.let {
                            Text(
                                text = it,
                                color = Color(0xFFFF6B6B),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        // Input Repetir contraseña
                        PasswordInput(
                            value = repeatPassword,
                            onValueChange = {
                                repeatPassword = it
                                repeatPasswordError = null
                            },
                            placeholder = "Repetir contraseña"
                        )
                        repeatPasswordError?.let {
                            Text(
                                text = it,
                                color = Color(0xFFFF6B6B),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                // Botón "Enviar solicitud"
                PrimaryButton(
                    text = "Enviar solicitud",
                    onClick = {
                        // Reset errores globales antes de validar
                        usernameError = null
                        emailError = null
                        phoneError = null
                        passwordError = null
                        repeatPasswordError = null

                        var valid = true

                        // Validaciones LoginLogic
                        try {
                            loginLogic.validateName(username)
                        } catch (e: IllegalArgumentException) {
                            usernameError = e.message
                            valid = false
                        }

                        try {
                            loginLogic.validateEmail(email)
                        } catch (e: IllegalArgumentException) {
                            emailError = e.message
                            valid = false
                        }

                        try {
                            loginLogic.validatePhone(phone)
                        } catch (e: IllegalArgumentException) {
                            phoneError = e.message
                            valid = false
                        }

                        try {
                            loginLogic.validatePassword(password)
                        } catch (e: IllegalArgumentException) {
                            passwordError = e.message
                            valid = false
                        }

                        try {
                            loginLogic.validateRepeat(password, repeatPassword)
                        } catch (e: IllegalArgumentException) {
                            repeatPasswordError = e.message
                            valid = false
                        }

                        // Si hay algún error, no se continúa con la navegación
                        if (!valid) {
                            return@PrimaryButton
                        }

                        // Si todo es válido, se navega al login
                        // y se elimina la pantalla de registro del backstack.
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

                // Espacio inferior
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}
