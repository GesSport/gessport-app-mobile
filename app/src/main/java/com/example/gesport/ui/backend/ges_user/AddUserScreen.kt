package com.example.gesport.ui.backend.ges_user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gesport.R
import com.example.gesport.domain.LoginLogic
import com.example.gesport.models.User
import com.example.gesport.models.UserRoles
import com.example.gesport.ui.components.GeSportBackgroundScreen
import com.example.gesport.ui.components.Input
import com.example.gesport.ui.components.PasswordInput
import com.example.gesport.ui.components.PrimaryButton

/**
 * Pantalla de alta / edición de usuario.
 *
 * - Si `userId` es null → modo creación de usuario.
 * - Si `userId` tiene valor → modo edición (carga los datos del usuario existente).
 *
 * Utiliza:
 * - GesUserViewModel para leer/guardar usuarios en el repositorio.
 * - LoginLogic para reutilizar las validaciones de nombre, email y contraseñasss.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(
    navController: NavHostController,
    viewModel: GesUserViewModel,
    userId: Int? = null
) {

    //
    val loginLogic = remember { LoginLogic() }

    // true si estamos editando un usuario existente
    val isEditMode = userId != null

    // Estados de los campos del formulario
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("JUGADOR") } // por defecto

    // Estados de error por campo
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var repeatPasswordError by remember { mutableStateOf<String?>(null) }
    var formError by remember { mutableStateOf<String?>(null) }

    // Error global gestionado desde el ViewModel (por ejemplo, error al guardar)
    val vmError = viewModel.errorMessage

    // Cargar datos si estamos editando
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadUserById(userId) { user ->
                if (user != null) {
                    // Rellenamos el formulario con los datos del usuario
                    name = user.nombre
                    email = user.email
                    password = user.password
                    repeatPassword = user.password
                    rol = user.rol
                } else {
                    // El id no existe en el repositorio
                    formError = "No se ha encontrado el usuario"
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GeSportBackgroundScreen {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Column {
                    // Header con título y subtítulo, y botón atrás arriba
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        // Título + subtítulo
                        Column(
                            modifier = Modifier.align(Alignment.BottomStart),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = if (isEditMode) "Editar usuario" else "Nuevo usuario",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = if (isEditMode)
                                    "Actualiza la información del usuario registrado en el sistema."
                                else
                                    "Completa los datos para registrar un nuevo usuario en GeSport.",
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Input Nombre
                    Input(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = null
                            formError = null
                        },
                        placeholder = "Nombre completo",
                        leadingIconRes = R.drawable.icon_user
                    )
                    if (nameError != null) {
                        Text(
                            text = nameError ?: "",
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Input Email
                    Input(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                            formError = null
                        },
                        placeholder = "Correo electrónico",
                        leadingIconRes = R.drawable.icon_email
                    )
                    if (emailError != null) {
                        Text(
                            text = emailError ?: "",
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Input Password
                    PasswordInput(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                            formError = null
                        },
                        placeholder = "Contraseña"
                    )
                    if (passwordError != null) {
                        Text(
                            text = passwordError ?: "",
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Input Repetir contraseña
                    PasswordInput(
                        value = repeatPassword,
                        onValueChange = {
                            repeatPassword = it
                            repeatPasswordError = null
                            formError = null
                        },
                        placeholder = "Repetir contraseña"
                    )
                    if (repeatPasswordError != null) {
                        Text(
                            text = repeatPasswordError ?: "",
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Chips Rol
                    Text(
                        text = "Rol de usuario",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        // Un chip por cada rol disponible en la app
                        UserRoles.allRoles.forEach { (roleKey, roleLabel) ->
                            FilterChip(
                                selected = rol == roleKey,
                                onClick = { rol = roleKey },
                                label = { Text(roleLabel) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = Color.White.copy(alpha = 0.20f),
                                    labelColor = Color.White,
                                    selectedContainerColor = Color(0xFF2DAAE1).copy(alpha = 0.40f),
                                    selectedLabelColor = Color.White
                                ),
                                border = null
                            )
                        }
                    }

                    // Errores globales
                    if (!vmError.isNullOrEmpty()) {
                        Text(
                            text = vmError,
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    if (!formError.isNullOrEmpty()) {
                        Text(
                            text = formError ?: "",
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Espacio para separar el formulario del botón inferior
                Spacer(Modifier.height(200.dp))

                // Botón Guardar
                PrimaryButton(
                    text = if (isEditMode) "Guardar cambios" else "Crear usuario",
                    onClick = {
                        // Reset de errores antes de validar
                        nameError = null
                        emailError = null
                        passwordError = null
                        repeatPasswordError = null
                        formError = null

                        // Validaciones usando LoginLogic
                        try {
                            loginLogic.validateName(name)
                        } catch (e: IllegalArgumentException) {
                            nameError = e.message
                        }

                        try {
                            loginLogic.validateEmail(email)
                        } catch (e: IllegalArgumentException) {
                            emailError = e.message
                        }

                        try {
                            loginLogic.validatePassword(password)
                        } catch (e: IllegalArgumentException) {
                            passwordError = e.message
                        }

                        try {
                            loginLogic.validateRepeat(password, repeatPassword)
                        } catch (e: IllegalArgumentException) {
                            repeatPasswordError = e.message
                        }

                        // Solo continúa si no hay errores de validación
                        val isValid = nameError == null &&
                                emailError == null &&
                                passwordError == null &&
                                repeatPasswordError == null

                        if (!isValid) return@PrimaryButton

                        // Construimos el objeto User a guardar
                        val user = User(
                            // En modo crear se ignora y el repo asigna ID
                            id = userId ?: 0,
                            nombre = name.trim(),
                            email = email.trim(),
                            password = password,
                            rol = rol
                        )

                        // Llamamos al ViewModel según el modo
                        if (isEditMode) {
                            viewModel.updateUser(user)
                        } else {
                            viewModel.addUser(user)
                        }

                        // Volver al listado de usuarios
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
