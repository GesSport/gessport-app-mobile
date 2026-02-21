package com.example.gesport.ui.backend.ges_user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(
    navController: NavHostController,
    viewModel: GesUserViewModel,
    userId: Int? = null
) {
    val loginLogic = remember { LoginLogic() }
    val isEditMode = userId != null

    // Estados del formulario
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf(UserRoles.JUGADOR) }

    // Errores
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var repeatPasswordError by remember { mutableStateOf<String?>(null) }
    var formError by remember { mutableStateOf<String?>(null) }

    val vmError = viewModel.errorMessage

    // Cargar datos si editamos
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadUserById(userId) { user ->
                if (user != null) {
                    name = user.nombre
                    email = user.email
                    password = user.password
                    repeatPassword = user.password
                    rol = user.rol
                } else {
                    formError = "No se ha encontrado el usuario"
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GeSportBackgroundScreen {

            // Layout clave: formulario scroll + botón fijo abajo
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {

                // BLOQUE SCROLLEABLE
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top
                ) {

                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
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
                    nameError?.let {
                        Text(
                            text = it,
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
                    emailError?.let {
                        Text(
                            text = it,
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
                    passwordError?.let {
                        Text(
                            text = it,
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
                    repeatPasswordError?.let {
                        Text(
                            text = it,
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
                    formError?.let {
                        Text(
                            text = it,
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                }

                // BOTÓN FIJO
                PrimaryButton(
                    text = if (isEditMode) "Guardar cambios" else "Crear usuario",
                    onClick = {
                        nameError = null
                        emailError = null
                        passwordError = null
                        repeatPasswordError = null
                        formError = null

                        try { loginLogic.validateName(name) } catch (e: IllegalArgumentException) { nameError = e.message }
                        try { loginLogic.validateEmail(email) } catch (e: IllegalArgumentException) { emailError = e.message }
                        try { loginLogic.validatePassword(password) } catch (e: IllegalArgumentException) { passwordError = e.message }
                        try { loginLogic.validateRepeat(password, repeatPassword) } catch (e: IllegalArgumentException) { repeatPasswordError = e.message }

                        val isValid = nameError == null &&
                                emailError == null &&
                                passwordError == null &&
                                repeatPasswordError == null

                        if (!isValid) return@PrimaryButton

                        val user = User(
                            id = userId ?: 0,
                            nombre = name.trim(),
                            email = email.trim(),
                            password = password,
                            rol = rol
                        )

                        if (isEditMode) viewModel.updateUser(user) else viewModel.addUser(user)
                        navController.popBackStack()
                    }
                )

                Spacer(Modifier.height(35.dp))
            }
        }
    }
}