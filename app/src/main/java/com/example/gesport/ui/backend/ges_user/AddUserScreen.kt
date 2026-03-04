package com.example.gesport.ui.backend.ges_user

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gesport.R
import com.example.gesport.data.RoomTeamRepository
import com.example.gesport.database.AppDatabase
import com.example.gesport.domain.LoginLogic
import com.example.gesport.models.Team
import com.example.gesport.models.User
import com.example.gesport.models.UserRoles
import com.example.gesport.ui.components.DatePickerField
import com.example.gesport.ui.components.GeSportBackgroundScreen
import com.example.gesport.ui.components.Input
import com.example.gesport.ui.components.PasswordInput
import com.example.gesport.ui.components.PrimaryButton
import com.example.gesport.ui.components.SelectField
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(
    navController: NavHostController,
    viewModel: GesUserViewModel,
    userId: Int? = null,
    // Rrol del usuario que realiza la acción
    currentUserRole: String = ""
) {
    val loginLogic  = remember { LoginLogic() }
    val isEditMode  = userId != null
    // Solo el admin puede cambiar roles y crear usuarios con cualquier rol
    val isAdmin     = currentUserRole.trim() == UserRoles.ADMIN_DEPORTIVO
    // Un usuario editando su propio perfil (no admin)
    val isSelfEdit  = isEditMode && !isAdmin

    val context = LocalContext.current
    val db      = remember { AppDatabase.getDatabase(context.applicationContext) }
    val teamRepo = remember { RoomTeamRepository(db.teamDao()) }

    var name           by remember { mutableStateOf("") }
    var email          by remember { mutableStateOf("") }
    var phone          by remember { mutableStateOf("") }
    var birthDate      by remember { mutableStateOf("") }
    var rol            by remember { mutableStateOf(UserRoles.JUGADOR) }
    var position       by remember { mutableStateOf("") }
    var selectedTeamLabel by remember { mutableStateOf("") }
    var selectedTeamId by remember { mutableStateOf<Int?>(null) }
    var password       by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var isSubmitting   by remember { mutableStateOf(false) }

    var nameError          by remember { mutableStateOf<String?>(null) }
    var emailError         by remember { mutableStateOf<String?>(null) }
    var phoneError         by remember { mutableStateOf<String?>(null) }
    var birthDateError     by remember { mutableStateOf<String?>(null) }
    var positionError      by remember { mutableStateOf<String?>(null) }
    var teamError          by remember { mutableStateOf<String?>(null) }
    var passwordError      by remember { mutableStateOf<String?>(null) }
    var repeatPasswordError by remember { mutableStateOf<String?>(null) }
    var formError          by remember { mutableStateOf<String?>(null) }

    val vmError    = viewModel.errorMessage
    val formatter  = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val today      = remember { LocalDate.now(ZoneId.of("Europe/Madrid")) }
    val minAllowed = remember { today.minusYears(90) }
    val maxAllowed = remember { today.minusYears(6) }

    fun parseBirthDate(value: String): LocalDate? = runCatching {
        LocalDate.parse(value.trim(), formatter)
    }.getOrNull()

    var teams by remember { mutableStateOf<List<Team>>(emptyList()) }
    LaunchedEffect(Unit) { teamRepo.getAllTeams().collectLatest { teams = it } }

    val teamOptions = remember(teams) { teams.map { "${it.id} · ${it.nombre} (${it.categoria})" } }
    fun findTeamIdFromLabel(label: String): Int? = label.substringBefore("·").trim().toIntOrNull()

    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadUserById(userId) { user ->
                if (user != null) {
                    name           = user.nombre
                    email          = user.email
                    phone          = user.telefono.orEmpty()
                    birthDate      = user.fechaNacimiento.orEmpty()
                    password       = user.password
                    repeatPassword = user.password
                    rol            = user.rol
                    position       = user.posicion.orEmpty()
                    selectedTeamId = user.equipoId
                } else {
                    formError = "No se ha encontrado el usuario"
                }
            }
        }
    }

    LaunchedEffect(teams, selectedTeamId) {
        val id = selectedTeamId ?: return@LaunchedEffect
        val t = teams.firstOrNull { it.id == id }
        selectedTeamLabel = if (t != null) "${t.id} · ${t.nombre} (${t.categoria})" else ""
    }

    LaunchedEffect(rol) {
        formError = null
        if (rol != UserRoles.JUGADOR) {
            position = ""; selectedTeamId = null; selectedTeamLabel = ""
            positionError = null; teamError = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GeSportBackgroundScreen {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {

                Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                    Column(modifier = Modifier.align(Alignment.BottomStart)) {
                        Text(
                            text = when {
                                isSelfEdit -> "Mi perfil"
                                isEditMode -> "Editar usuario"
                                else       -> "Nuevo usuario"
                            },
                            color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = when {
                                isSelfEdit -> "Actualiza tu información personal."
                                isEditMode -> "Actualiza la información del usuario registrado."
                                else       -> "Completa los datos para registrar un nuevo usuario."
                            },
                            color = Color.White.copy(alpha = 0.65f), fontSize = 15.sp
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // SELECTOR DE ROL: solo visible para admins
                // Un jugador/entrenador NO puede cambiar su propio rol
                if (isAdmin) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        UserRoles.allRoles.forEach { (roleKey, roleLabel) ->
                            FilterChip(
                                selected = rol == roleKey,
                                onClick = { rol = roleKey; formError = null },
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
                    Spacer(Modifier.height(16.dp))
                } else {
                    val roleLabel = UserRoles.allRoles[rol] ?: rol
                    Text(
                        text = "Rol: $roleLabel",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top
                ) {
                    Input(value = name, onValueChange = { name = it; nameError = null; formError = null }, placeholder = "Nombre completo", leadingIconRes = R.drawable.icon_user)
                    nameError?.let { Text(it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) }

                    Spacer(Modifier.height(8.dp))

                    Input(value = email, onValueChange = { email = it; emailError = null; formError = null }, placeholder = "Correo electrónico", leadingIconRes = R.drawable.icon_email)
                    emailError?.let { Text(it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) }

                    Spacer(Modifier.height(8.dp))

                    Input(value = phone, onValueChange = { phone = it; phoneError = null; formError = null }, placeholder = "Teléfono", leadingIconRes = R.drawable.icon_phone)
                    phoneError?.let { Text(it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) }

                    Spacer(Modifier.height(8.dp))

                    DatePickerField(value = birthDate, onDateSelected = { birthDate = it; birthDateError = null; formError = null }, placeholder = "Fecha de nacimiento", leadingIconRes = R.drawable.icon_reservation)
                    birthDateError?.let { Text(it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) }

                    if (rol == UserRoles.JUGADOR) {
                        Spacer(Modifier.height(12.dp))
                        SelectField(
                            value = selectedTeamLabel,
                            onSelected = { picked -> selectedTeamLabel = picked; selectedTeamId = findTeamIdFromLabel(picked); teamError = null; formError = null },
                            placeholder = "Asignar equipo (opcional)",
                            leadingIconVector = Icons.Default.People,
                            leadingIconRes = R.drawable.icon_user,
                            options = teamOptions
                        )
                        teamError?.let { Text(it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) }

                        Spacer(Modifier.height(8.dp))
                        Input(value = position, onValueChange = { position = it; positionError = null; formError = null }, placeholder = "Posición (opcional)", leadingIconRes = R.drawable.icon_user)
                        positionError?.let { Text(it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) }
                    }

                    Spacer(Modifier.height(12.dp))

                    PasswordInput(value = password, onValueChange = { password = it; passwordError = null; formError = null }, placeholder = "Contraseña")
                    passwordError?.let { Text(it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) }

                    Spacer(Modifier.height(8.dp))

                    PasswordInput(value = repeatPassword, onValueChange = { repeatPassword = it; repeatPasswordError = null; formError = null }, placeholder = "Repetir contraseña")
                    repeatPasswordError?.let { Text(it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) }

                    if (!vmError.isNullOrEmpty()) Text(vmError, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 4.dp))
                    formError?.let { Text(it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 4.dp)) }

                    Spacer(Modifier.height(16.dp))
                }

                PrimaryButton(
                    text = when {
                        isSelfEdit -> "Guardar cambios"
                        isEditMode -> "Guardar cambios"
                        else       -> "Crear usuario"
                    },
                    enabled = !isSubmitting,
                    onClick = {
                        if (isSubmitting) return@PrimaryButton
                        isSubmitting = true
                        nameError = null; emailError = null; phoneError = null; birthDateError = null
                        teamError = null; positionError = null; passwordError = null; repeatPasswordError = null; formError = null

                        var valid = true

                        try { loginLogic.validateName(name) } catch (e: IllegalArgumentException) { nameError = e.message; valid = false }
                        try { loginLogic.validateEmail(email) } catch (e: IllegalArgumentException) { emailError = e.message; valid = false }
                        try { loginLogic.validatePhone(phone) } catch (e: IllegalArgumentException) { phoneError = e.message; valid = false }

                        val parsed = parseBirthDate(birthDate)
                        if (parsed == null) { birthDateError = "Selecciona una fecha válida."; valid = false }
                        else when {
                            parsed.isAfter(today)    -> { birthDateError = "La fecha no puede ser futura."; valid = false }
                            parsed.isBefore(minAllowed) -> { birthDateError = "La fecha es demasiado antigua."; valid = false }
                            parsed.isAfter(maxAllowed)  -> { birthDateError = "Debe tener al menos 6 años."; valid = false }
                        }

                        try { loginLogic.validatePassword(password) } catch (e: IllegalArgumentException) { passwordError = e.message; valid = false }
                        try { loginLogic.validateRepeat(password, repeatPassword) } catch (e: IllegalArgumentException) { repeatPasswordError = e.message; valid = false }

                        if (!valid) { isSubmitting = false; return@PrimaryButton }

                        val finalRol = if (isAdmin) rol else {
                            rol
                        }

                        val user = User(
                            id              = userId ?: 0,
                            nombre          = name.trim(),
                            email           = email.trim(),
                            password        = password,
                            rol             = finalRol,
                            fechaNacimiento = birthDate.trim().ifBlank { null },
                            telefono        = phone.trim().ifBlank { null },
                            posicion        = if (rol == UserRoles.JUGADOR) position.trim().ifBlank { null } else null,
                            equipoId        = if (rol == UserRoles.JUGADOR) selectedTeamId else null
                        )

                        if (isEditMode) viewModel.updateUser(user) else viewModel.addUser(user)
                        isSubmitting = false
                        navController.popBackStack()
                    }
                )

                Spacer(Modifier.height(35.dp))
            }
        }
    }
}