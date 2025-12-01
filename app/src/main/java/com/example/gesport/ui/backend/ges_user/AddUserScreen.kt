package com.example.gesport.ui.backend.ges_user

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gesport.R
import com.example.gesport.data.DataUserRepository
import com.example.gesport.models.User
import com.example.gesport.models.UserRoles
import com.example.gesport.ui.components.Input
import com.example.gesport.ui.components.PasswordInput
import com.example.gesport.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(
    navController: NavHostController,
    userId: Int? = null
) {
    // ViewModel
    val viewModel: GesUserViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = DataUserRepository
                return GesUserViewModel(repo) as T
            }
        }
    )

    val isEditMode = userId != null

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("JUGADOR") } // por defecto

    var nombreError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var formError by remember { mutableStateOf<String?>(null) }

    val vmError = viewModel.errorMessage
    val isLoading = viewModel.isLoading

    // Cargar datos si estamos editando
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadUserById(userId) { user ->
                if (user != null) {
                    nombre = user.name
                    email = user.email
                    password = user.password
                    rol = user.rol
                } else {
                    formError = "No se ha encontrado el usuario"
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fondo con imagen (mismo patrón que login y gestión de usuarios)
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                // 🔹 HEADER + FORM
                Column {

                    // Header con título y subtítulo, y botón atrás arriba
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        // Botón atrás
                        TextButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Text("< Atrás", color = Color(0xFF2DAAE1))
                        }

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

                    // NOMBRE
                    Input(
                        value = nombre,
                        onValueChange = {
                            nombre = it
                            nombreError = null
                            formError = null
                        },
                        placeholder = "Nombre completo",
                        leadingIconRes = R.drawable.icon_user
                    )
                    if (nombreError != null) {
                        Text(
                            text = nombreError ?: "",
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // EMAIL
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

                    // PASSWORD
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

                    Spacer(Modifier.height(12.dp))

                    // ROL
                    Text(
                        text = "Rol de usuario",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
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

                // 🔹 BOTÓN GUARDAR ABAJO (mismo estilo que en login)
                PrimaryButton(
                    text = if (isEditMode) "Guardar cambios" else "Crear usuario",
                    onClick = {
                        // Reset de errores
                        nombreError = null
                        emailError = null
                        passwordError = null
                        formError = null

                        var isValid = true

                        if (nombre.isBlank()) {
                            nombreError = "Introduce un nombre"
                            isValid = false
                        }
                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            emailError = "Introduce un email válido"
                            isValid = false
                        }
                        if (password.length < 4) {
                            passwordError = "La contraseña debe tener al menos 4 caracteres"
                            isValid = false
                        }

                        if (!isValid) return@PrimaryButton

                        val user = User(
                            id = userId ?: 0,
                            name = nombre.trim(),
                            email = email.trim(),
                            password = password,
                            rol = rol
                        )

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

        // Si quisieras, aquí podrías mostrar un overlay de loading con isLoading
    }
}
