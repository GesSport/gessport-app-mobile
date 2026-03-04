package com.example.gesport.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gesport.R
import com.example.gesport.data.RoomUserRepository
import com.example.gesport.database.AppDatabase
import com.example.gesport.domain.LoginLogic
import com.example.gesport.models.User
import com.example.gesport.models.UserRoles
import com.example.gesport.ui.components.DatePickerField
import com.example.gesport.ui.components.GeSportBackgroundScreen
import com.example.gesport.ui.components.Input
import com.example.gesport.ui.components.PasswordInput
import com.example.gesport.ui.components.PrimaryButton
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController) {
    val loginLogic = remember { LoginLogic() }
    val scope = rememberCoroutineScope()

    // ROOM
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context.applicationContext) }
    val repo = remember { RoomUserRepository(db.userDao()) }

    // Campos
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var birthDate by rememberSaveable { mutableStateOf("") } // yyyy-MM-dd
    var password by rememberSaveable { mutableStateOf("") }
    var repeatPassword by rememberSaveable { mutableStateOf("") }

    // Rol por chips
    var rol by rememberSaveable { mutableStateOf(UserRoles.JUGADOR) }

    // Errores
    var usernameError by rememberSaveable { mutableStateOf<String?>(null) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var phoneError by rememberSaveable { mutableStateOf<String?>(null) }
    var birthDateError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var repeatPasswordError by rememberSaveable { mutableStateOf<String?>(null) }
    var formError by rememberSaveable { mutableStateOf<String?>(null) }

    // Helpers fecha nacimiento
    val formatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val today = remember { LocalDate.now(ZoneId.of("Europe/Madrid")) }

    fun parseBirthDate(value: String): LocalDate? {
        val v = value.trim()
        if (v.isBlank()) return null
        return try {
            LocalDate.parse(v, formatter)
        } catch (_: DateTimeParseException) {
            null
        }
    }

    // Rango razonable: 6..90 años
    val minAgeYears = 6
    val maxAgeYears = 90
    val minAllowed = remember { today.minusYears(maxAgeYears.toLong()) } // más viejo permitido
    val maxAllowed = remember { today.minusYears(minAgeYears.toLong()) } // más joven permitido

    Box(Modifier.fillMaxSize()) {
        GeSportBackgroundScreen {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {

                // ========= HEADER FIJO (NO SCROLL) =========
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Registro de usuario",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Completa el formulario con tus datos personales para crear una nueva cuenta en GeSport.",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ========= BLOQUE SCROLLEABLE =========
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Chips rol
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        UserRoles.registerRoles.forEach { roleKey ->
                            val roleLabel = UserRoles.allRoles[roleKey] ?: roleKey

                            FilterChip(
                                selected = rol == roleKey,
                                onClick = {
                                    rol = roleKey
                                    formError = null
                                },
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

                    // Nombre
                    Input(
                        value = username,
                        onValueChange = {
                            username = it
                            usernameError = null
                            formError = null
                        },
                        placeholder = "Nombre de usuario",
                        leadingIconRes = R.drawable.icon_user
                    )
                    usernameError?.let {
                        Text(text = it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 2.dp))
                    }

                    // Email
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
                        Text(text = it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 2.dp))
                    }

                    // Teléfono
                    Input(
                        value = phone,
                        onValueChange = {
                            phone = it
                            phoneError = null
                            formError = null
                        },
                        placeholder = "Teléfono",
                        leadingIconRes = R.drawable.icon_phone
                    )
                    phoneError?.let {
                        Text(text = it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 2.dp))
                    }

                    // Fecha nacimiento
                    DatePickerField(
                        value = birthDate,
                        onDateSelected = {
                            birthDate = it
                            birthDateError = null
                            formError = null
                        },
                        placeholder = "Fecha de nacimiento",
                        leadingIconRes = R.drawable.icon_reservation
                    )
                    birthDateError?.let {
                        Text(text = it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 2.dp))
                    }

                    // Contraseña
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
                        Text(text = it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 2.dp))
                    }

                    // Repetir contraseña
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
                        Text(text = it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 2.dp))
                    }

                    // Error general (lo dejamos dentro del scroll, justo debajo del formulario)
                    formError?.let {
                        Text(text = it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 4.dp))
                    }

                    Spacer(Modifier.height(8.dp))
                }

                Spacer(Modifier.height(10.dp))

                // ========= BOTÓN + FOOTER FIJOS (NO SCROLL) =========
                PrimaryButton(
                    text = "Registrarse",
                    onClick = {
                        usernameError = null
                        emailError = null
                        phoneError = null
                        birthDateError = null
                        passwordError = null
                        repeatPasswordError = null
                        formError = null

                        var valid = true

                        try { loginLogic.validateName(username) }
                        catch (e: IllegalArgumentException) { usernameError = e.message; valid = false }

                        try { loginLogic.validateEmail(email) }
                        catch (e: IllegalArgumentException) { emailError = e.message; valid = false }

                        try { loginLogic.validatePhone(phone) }
                        catch (e: IllegalArgumentException) { phoneError = e.message; valid = false }

                        // Validación fecha nacimiento (obligatoria + rango)
                        val parsedBirth = parseBirthDate(birthDate)
                        if (parsedBirth == null) {
                            birthDateError = "Selecciona una fecha válida."
                            valid = false
                        } else {
                            when {
                                parsedBirth.isAfter(today) -> {
                                    birthDateError = "La fecha no puede ser futura."
                                    valid = false
                                }
                                parsedBirth.isBefore(minAllowed) -> {
                                    birthDateError = "La fecha es demasiado antigua."
                                    valid = false
                                }
                                parsedBirth.isAfter(maxAllowed) -> {
                                    birthDateError = "Debes tener al menos $minAgeYears años."
                                    valid = false
                                }
                            }
                        }

                        try { loginLogic.validatePassword(password) }
                        catch (e: IllegalArgumentException) { passwordError = e.message; valid = false }

                        try { loginLogic.validateRepeat(password, repeatPassword) }
                        catch (e: IllegalArgumentException) { repeatPasswordError = e.message; valid = false }

                        if (!valid) return@PrimaryButton

                        scope.launch {
                            try {
                                val mail = email.trim()

                                val existing = repo.getUserByEmail(mail)
                                if (existing != null) {
                                    formError = "Ese correo ya está registrado."
                                    return@launch
                                }

                                val user = User(
                                    nombre = username.trim(),
                                    email = mail,
                                    password = password,
                                    rol = rol,
                                    telefono = phone.trim().ifBlank { null },
                                    fechaNacimiento = birthDate.trim().ifBlank { null }
                                )

                                repo.addUser(user)

                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            } catch (_: Exception) {
                                formError = "No se ha podido registrar el usuario."
                            }
                        }
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, bottom = 4.dp),
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

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}