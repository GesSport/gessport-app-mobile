package com.example.gesport.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gesport.R
import com.example.gesport.domain.LoginLogic
import com.example.gesport.ui.components.GeSportBackgroundScreen
import com.example.gesport.ui.components.GoogleButton
import com.example.gesport.ui.components.Input
import com.example.gesport.ui.components.PasswordInput
import com.example.gesport.ui.components.PrimaryButton
import kotlinx.coroutines.launch

/**
 * Pantalla de inicio de sesión.
 *
 * Gestiona:
 * - Entrada de email y contraseña.
 * - Recuerdo de sesión (checkbox).
 * - Navegación a recuperación de contraseña, registro y dashboard/home según el rol.
 * - Uso de LoginLogic para validar credenciales contra el repositorio de usuarios.
 */
@Composable
fun LoginScreen(navController: NavHostController) {
    // Instancia de la lógica de validación
    val logic = remember { LoginLogic() }

    // Scope para lanzar corrutinas desde la UI (checkLogin es suspend).
    val scope = rememberCoroutineScope()

    // Estado de los campos del formulario
    var email by remember { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var rememberMe by rememberSaveable { mutableStateOf(false) }

    // Mensaje de error mostrado debajo de la contraseña (si falla el login)
    var errorMessage by remember { mutableStateOf("") }

    // Componente reutilizable que aplica el fondo y la capa oscura
    GeSportBackgroundScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // Contenedor Título + Logo + Subtítulo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.BottomStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        horizontalAlignment = Alignment.Start,
                    ) {
                        // Texto de bienvenida
                        Text(
                            text = "Bienvenido a",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.height(15.dp))

                        // Logo + GeSport
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.logo),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = "GeSport",
                                color = Color.White,
                                fontSize = 46.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(Modifier.height(2.dp))

                        // Descripción breve
                        Text(
                            text = "Centro Multideporte de Alto Rendimiento. " +
                                    "\nGestión de usuarios, equipos, pistas, " +
                                    "\ny reservas.",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(Modifier.height(1.dp))

            // Formulario
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Input Correo electrónico
                Input(
                    value = email,
                    onValueChange = {
                        email = it
                        // Si había error, se limpia al modificar el campo
                        if (errorMessage.isNotEmpty()) errorMessage = ""
                    },
                    placeholder = "Correo electrónico",
                    leadingIconRes = R.drawable.icon_email
                )

                Spacer(Modifier.height(1.dp))

                // Input Contraseña
                PasswordInput(
                    value = password,
                    onValueChange = {
                        password = it
                        // Limpia el error al escribir de nuevo
                        if (errorMessage.isNotEmpty()) errorMessage = ""
                    },
                    placeholder = "Contraseña"
                )

                // Mensaje de error general del login
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Checkbox "Recuérdame" + botón "¿Olvidaste…? Pulsa aquí"
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Recuérdame
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF2DAAE1).copy(alpha = 0.55f),
                                uncheckedColor = Color.White,
                                checkmarkColor = Color.White,
                                disabledUncheckedColor = Color.Transparent
                            )
                        )
                        Text("Recuérdame", color = Color.White)
                    }

                    Spacer(Modifier.height(5.dp))

                    // botón "¿Olvidaste…? Pulsa aquí
                    Row(
                        modifier = Modifier.padding(top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "¿Olvidaste tu contraseña?",
                            color = Color.White
                        )
                        TextButton(
                            onClick = { navController.navigate("recover") },
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFF2DAAE1).copy(alpha = 0.55f)
                            )
                        ) {
                            Text("Pulsa aquí")
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Botón "Iniciar sesión"
                PrimaryButton(
                    text = "Iniciar sesión",
                    onClick = {
                        errorMessage = ""
                        scope.launch {
                            try {
                                // Validación del login mediante LoginLogic.
                                // Si las credenciales son correctas, devuelve el usuario.
                                val user = logic.checkLogin(
                                    email.trim(),
                                    password
                                )

                                // Navegación según el rol del usuario autenticado
                                if (user.rol == "ADMIN_DEPORTIVO") {
                                    navController.navigate("dashboard/${user.name}")
                                } else {
                                    navController.navigate("home/${user.name}")
                                }
                            } catch (e: IllegalArgumentException) {
                                // Errores controlados de validación (campos vacíos, credenciales incorrectas, etc.)
                                errorMessage = e.message.toString()
                            } catch (_: Exception) {
                                // Errores genéricos no esperados
                                errorMessage = "Ha ocurrido un error"
                            }
                        }
                    }
                )

                // Botón "Iniciar sesión con Google"
                GoogleButton(
                    onClick = {
                        // TODO: Implementar inicio de sesión con Google
                    }
                )

                // Botón “¿No tienes cuenta? Crea una aquí”
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("¿No tienes cuenta?", color = Color.White)
                    TextButton(
                        onClick = { navController.navigate("register") },
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF2DAAE1).copy(alpha = 0.55f)
                        )
                    ) {
                        Text("Crea una aquí")
                    }
                }
            }

        }
    }
}
