package com.example.gesport.ui.backend.ges_reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsBaseball
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gesport.R
import com.example.gesport.models.Reservation
import com.example.gesport.models.UserRoles
import com.example.gesport.ui.components.DatePickerField
import com.example.gesport.ui.components.GeSportBackgroundScreen
import com.example.gesport.ui.components.PrimaryButton
import com.example.gesport.ui.components.SelectField
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private fun parseToLocalDate(dateStr: String): LocalDate? {
    val value = dateStr.trim()
    if (value.isBlank()) return null
    val patterns = listOf("dd/MM/yyyy", "d/M/yyyy", "yyyy-MM-dd")
    for (p in patterns) {
        try { return LocalDate.parse(value, DateTimeFormatter.ofPattern(p)) }
        catch (_: DateTimeParseException) {}
    }
    return null
}

private enum class ReservationKind { PERSONAL, TEAM }

private fun teamLabel(id: Int, name: String, cat: String): String = "$id · $name ($cat)"
private fun userLabel(name: String, email: String): String = "$name · $email"
private fun parseIdFromLabel(label: String): Int? = label.substringBefore("·").trim().toIntOrNull()

private val ALL_DAY_SLOTS = generateSlots(dayStart = "08:00", dayEnd = "22:00", slotMinutes = 60L)

@Composable
fun AddReservationScreen(
    navController: NavHostController,
    viewModel: GesReservationViewModel,
    currentUserId: Int,
    currentUserRole: String,
    reservationId: Int? = null
) {
    val isEditMode = reservationId != null

    val roleKey = remember(currentUserRole) {
        currentUserRole.trim().takeIf { it.isNotEmpty() } ?: UserRoles.JUGADOR
    }

    val isAdmin   = roleKey == UserRoles.ADMIN_DEPORTIVO
    val isTrainer = roleKey == UserRoles.ENTRENADOR
    val isPlayer  = !isAdmin && !isTrainer

    val isLoading = viewModel.isLoading
    val vmError   = viewModel.errorMessage

    val facilities = viewModel.facilities
    val users      = viewModel.users
    val teams      = viewModel.teams

    // Compose detecta el cambio de esta lista y recompone el grid de slots.
    val gridReservations = viewModel.reservations

    val facilityOptions = remember(facilities) { facilities.map { it.nombre } }

    val myTeams = remember(teams, currentUserId, isTrainer) {
        if (!isTrainer) emptyList() else teams.filter { it.entrenadorId == currentUserId }
    }
    val selectableTeams = remember(isAdmin, teams, myTeams) { if (isAdmin) teams else myTeams }
    val teamOptions     = remember(selectableTeams) {
        selectableTeams.map { teamLabel(it.id, it.nombre, it.categoria) }
    }
    val selectableUsers = remember(isAdmin, users) { if (isAdmin) users else emptyList() }
    val userOptions     = remember(selectableUsers) { selectableUsers.map { userLabel(it.nombre, it.email) } }

    // Form state
    var selectedDate         by remember { mutableStateOf("") }
    var selectedFacilityName by remember { mutableStateOf("") }
    var kind by remember {
        mutableStateOf(if (isPlayer) ReservationKind.PERSONAL else ReservationKind.TEAM)
    }
    var selectedUserLabel by remember { mutableStateOf("") }
    var selectedUserId    by remember { mutableStateOf<Int?>(null) }
    var selectedTeamLabel by remember { mutableStateOf("") }
    var selectedTeamId    by remember { mutableStateOf<Int?>(null) }
    var selectedUseType   by remember { mutableStateOf("") }
    var selectedStart     by remember { mutableStateOf<String?>(null) }
    var selectedEnd       by remember { mutableStateOf<String?>(null) }
    var originalStart     by remember { mutableStateOf<String?>(null) }
    var originalEnd       by remember { mutableStateOf<String?>(null) }
    var originalDate      by remember { mutableStateOf<String?>(null) }

    var dateError     by remember { mutableStateOf<String?>(null) }
    var facilityError by remember { mutableStateOf<String?>(null) }
    var userError     by remember { mutableStateOf<String?>(null) }
    var teamError     by remember { mutableStateOf<String?>(null) }
    var useTypeError  by remember { mutableStateOf<String?>(null) }
    var slotError     by remember { mutableStateOf<String?>(null) }
    var formError     by remember { mutableStateOf<String?>(null) }
    var isForbiddenEdit by remember { mutableStateOf(false) }

    val useTypeOptions = remember { listOf("Entrenamiento", "Partido", "Clase", "Torneo", "Libre") }
    val outFormatter   = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val today          = remember { LocalDate.now(ZoneId.of("Europe/Madrid")) }
    val inputsEnabled  = !isForbiddenEdit

    // Preselección instalación al arrancar
    LaunchedEffect(facilities, viewModel.selectedFacilityId) {
        val id = viewModel.selectedFacilityId
        if (id != null && selectedFacilityName.isBlank()) {
            selectedFacilityName = facilities.firstOrNull { it.id == id }?.nombre.orEmpty()
        }
    }

    // Usuario fijo para jugador/entrenador en reserva personal
    LaunchedEffect(users, isPlayer, isTrainer, kind, currentUserId, isEditMode) {
        if (isEditMode) return@LaunchedEffect
        if (kind != ReservationKind.PERSONAL) return@LaunchedEffect
        if (isPlayer || isTrainer) {
            val me = users.firstOrNull { it.id == currentUserId }
            selectedUserId    = currentUserId
            selectedUserLabel = me?.let { userLabel(it.nombre, it.email) }.orEmpty()
        }
    }

    // Entrenador sin equipos → forzar PERSONAL
    LaunchedEffect(isTrainer, kind, myTeams, isEditMode) {
        if (!isEditMode && isTrainer && kind == ReservationKind.TEAM && myTeams.isEmpty()) {
            formError = "No tienes equipos asignados. Solo puedes crear reservas personales."
            kind = ReservationKind.PERSONAL
            selectedTeamId = null; selectedTeamLabel = ""
        }
    }

    // Entrenador con 1 equipo → preseleccionar
    LaunchedEffect(isTrainer, kind, myTeams, isEditMode) {
        if (!isEditMode && isTrainer && kind == ReservationKind.TEAM && myTeams.size == 1) {
            val t = myTeams.first()
            selectedTeamId = t.id; selectedTeamLabel = teamLabel(t.id, t.nombre, t.categoria)
        }
    }

    // Cargar datos en modo edición
    LaunchedEffect(reservationId, teams) {
        if (reservationId == null) return@LaunchedEffect
        viewModel.loadReservationById(reservationId) { res ->
            if (res == null) { formError = "No se ha encontrado la reserva"; return@loadReservationById }

            isForbiddenEdit = when {
                isAdmin   -> false
                isTrainer -> {
                    val mine = (res.equipoId == null && res.usuarioId == currentUserId) ||
                            (res.equipoId != null && myTeams.any { it.id == res.equipoId })
                    !mine
                }
                else -> !(res.equipoId == null && res.usuarioId == currentUserId)
            }
            if (isForbiddenEdit) formError = "No tienes permisos para editar esta reserva."

            selectedDate         = res.fecha;        originalDate  = res.fecha
            selectedFacilityName = viewModel.getFacilityNameById(res.pistaId).orEmpty()
            kind = if (res.equipoId != null) ReservationKind.TEAM else ReservationKind.PERSONAL

            val u = res.usuarioId?.let { viewModel.getUserById(it) }
            selectedUserId = u?.id; selectedUserLabel = u?.let { userLabel(it.nombre, it.email) }.orEmpty()

            val t = res.equipoId?.let { viewModel.getTeamById(it) }
            selectedTeamId = t?.id; selectedTeamLabel = t?.let { teamLabel(it.id, it.nombre, it.categoria) }.orEmpty()

            selectedUseType = res.tipoUso.orEmpty()
            selectedStart   = res.horaInicio; originalStart = res.horaInicio
            selectedEnd     = res.horaFin;    originalEnd   = res.horaFin

            // Disparar carga del grid al editar
            viewModel.onDateSelected(res.fecha)
            viewModel.onFacilitySelected(res.pistaId)
        }
    }

    val selectedLocalDate = remember(selectedDate) { parseToLocalDate(selectedDate) }
    val isSelectedPast    = selectedLocalDate?.isBefore(today) == true

    // UI
    Box(modifier = Modifier.fillMaxSize()) {
        GeSportBackgroundScreen {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {

                // HEADER
                Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                    Column(modifier = Modifier.align(Alignment.BottomStart)) {
                        Text(
                            if (isEditMode) "Editar reserva" else "Nueva reserva",
                            color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("Selecciona fecha, pista y franja.", color = Color.White.copy(alpha = 0.65f), fontSize = 15.sp)
                    }
                }

                Spacer(Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 20.dp),
                    verticalArrangement = Arrangement.Top
                ) {

                    // Tipo de reserva
                    if (!isPlayer) {
                        Text("Tipo de reserva", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FilterChip(
                                selected = kind == ReservationKind.PERSONAL,
                                enabled  = inputsEnabled,
                                onClick  = {
                                    kind = ReservationKind.PERSONAL
                                    teamError = null
                                    selectedTeamId = null
                                    selectedTeamLabel = ""
                                },
                                label = { Text("Personal") },
                                leadingIcon = { Icon(Icons.Default.Person, null, tint = Color.White) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF2DAAE1).copy(alpha = 0.35f),
                                    containerColor = Color.White.copy(alpha = 0.20f),
                                    labelColor = Color.White,
                                    selectedLabelColor = Color.White
                                ),
                                border = null
                            )

                            FilterChip(
                                selected = kind == ReservationKind.TEAM,
                                enabled  = inputsEnabled && (isAdmin || (isTrainer && myTeams.isNotEmpty())),
                                onClick  = {
                                    kind = ReservationKind.TEAM
                                    userError = null
                                    if (isTrainer && myTeams.size == 1) {
                                        val t = myTeams.first()
                                        selectedTeamId = t.id
                                        selectedTeamLabel = teamLabel(t.id, t.nombre, t.categoria)
                                    }
                                },
                                label = { Text("Equipo") },
                                leadingIcon = { Icon(Icons.Default.Groups, null, tint = Color.White) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF2DAAE1).copy(alpha = 0.35f),
                                    containerColor = Color.White.copy(alpha = 0.20f),
                                    labelColor = Color.White,
                                    selectedLabelColor = Color.White
                                ),
                                border = null
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                    }

                    // Fecha
                    Text("Fecha", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    DatePickerField(
                        value = selectedDate,
                        onDateSelected = { pickedStr ->
                            if (!inputsEnabled) return@DatePickerField
                            formError = null; dateError = null
                            val picked = parseToLocalDate(pickedStr)
                            if (picked == null) { selectedDate = ""; dateError = "Formato no válido."; return@DatePickerField }
                            val canonical    = picked.format(outFormatter)
                            val keepOriginal = isEditMode && originalDate == canonical
                            if (picked.isBefore(today) && !keepOriginal) {
                                dateError = "No puedes reservar en una fecha pasada."; return@DatePickerField
                            }
                            selectedDate  = canonical
                            selectedStart = null; selectedEnd = null; slotError = null
                            if (!isEditMode) { originalStart = null; originalEnd = null; originalDate = null }

                            // Actualizar grid al cambiar fecha
                            viewModel.onDateSelected(canonical)
                            val facilityId = facilities.firstOrNull { it.nombre == selectedFacilityName }?.id
                            if (facilityId != null) viewModel.onFacilitySelected(facilityId)
                        },
                        placeholder    = "Selecciona una fecha",
                        leadingIconRes = R.drawable.icon_reservation
                    )
                    dateError?.let { Text(it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 6.dp)) }
                    if (isEditMode && isSelectedPast) {
                        Text("Reserva de fecha pasada: puedes editarla si tienes permiso.", color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(top = 6.dp))
                    }
                    Spacer(Modifier.height(12.dp))

                    // Instalación
                    Text("Instalación", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    SelectField(
                        value      = selectedFacilityName,
                        onSelected = { pickedName ->
                            if (!inputsEnabled) return@SelectField
                            selectedFacilityName = pickedName; formError = null; facilityError = null
                            selectedStart = null; selectedEnd = null; slotError = null
                            if (!isEditMode) { originalStart = null; originalEnd = null }

                            // ✅ FIX: actualizar grid inmediatamente al cambiar instalación
                            val pickedId = facilities.firstOrNull { it.nombre == pickedName }?.id
                            if (selectedDate.isNotBlank()) viewModel.onDateSelected(selectedDate)
                            viewModel.onFacilitySelected(pickedId)
                        },
                        placeholder       = "Selecciona una pista",
                        leadingIconRes    = R.drawable.icon_location,
                        leadingIconVector = Icons.Default.AutoAwesomeMosaic,
                        options           = facilityOptions
                    )
                    facilityError?.let { Text(it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 6.dp)) }
                    Spacer(Modifier.height(12.dp))

                    // Usuario (personal)
                    if (kind == ReservationKind.PERSONAL) {
                        Text("Usuario", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))
                        if (isPlayer || isTrainer) {
                            Text(selectedUserLabel.ifBlank { "Usuario actual (#$currentUserId)" }, color = Color.White.copy(alpha = 0.85f))
                        } else {
                            SelectField(
                                value      = selectedUserLabel,
                                onSelected = { picked ->
                                    if (!inputsEnabled) return@SelectField
                                    selectedUserLabel = picked; formError = null; userError = null
                                    val email = picked.substringAfter("·").trim()
                                    selectedUserId = users.firstOrNull { it.email.trim().equals(email, ignoreCase = true) }?.id
                                },
                                placeholder    = "Selecciona un usuario",
                                leadingIconRes = R.drawable.icon_user,
                                options        = userOptions
                            )
                        }
                        userError?.let { Text(it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 6.dp)) }
                        Spacer(Modifier.height(12.dp))
                    }

                    // Equipo
                    if (kind == ReservationKind.TEAM) {
                        Text("Equipo", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))
                        SelectField(
                            value      = selectedTeamLabel,
                            onSelected = { picked ->
                                if (!inputsEnabled) return@SelectField
                                selectedTeamLabel = picked; formError = null; teamError = null
                                selectedTeamId = selectableTeams.firstOrNull { it.id == parseIdFromLabel(picked) }?.id
                            },
                            placeholder       = if (isAdmin) "Selecciona un equipo" else "Selecciona uno de tus equipos",
                            leadingIconVector = Icons.Default.Groups,
                            leadingIconRes    = R.drawable.icon_user,
                            options           = teamOptions
                        )
                        teamError?.let { Text(it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 6.dp)) }
                        Spacer(Modifier.height(12.dp))
                    }

                    // Tipo de uso
                    Text("Tipo de uso", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    SelectField(
                        value      = selectedUseType,
                        onSelected = { if (!inputsEnabled) return@SelectField; selectedUseType = it; formError = null; useTypeError = null },
                        placeholder       = "Selecciona el tipo de uso",
                        leadingIconRes    = R.drawable.icon_reservation,
                        leadingIconVector = Icons.Default.SportsBaseball,
                        options           = useTypeOptions
                    )
                    useTypeError?.let { Text(it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 6.dp)) }
                    Spacer(Modifier.height(16.dp))

                    // Franja horaria
                    Text("Franja horaria", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))

                    when {
                        isLoading -> {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                CircularProgressIndicator(color = Color(0xFF2DAAE1), modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                Text("Cargando franjas...", color = Color.White.copy(alpha = 0.7f))
                            }
                        }
                        selectedDate.isBlank() || selectedFacilityName.isBlank() -> {
                            Text("Selecciona fecha e instalación para ver las franjas disponibles", color = Color.White.copy(alpha = 0.7f))
                        }
                        !isEditMode && isSelectedPast -> {
                            Text("No puedes reservar en fechas pasadas.", color = Color(0xFFFF6B6B).copy(alpha = 0.9f))
                        }
                        else -> {
                            val occupiedSlots by remember(gridReservations, reservationId) {
                                derivedStateOf {
                                    ALL_DAY_SLOTS.map { slot ->
                                        val occupied = gridReservations.any { res ->
                                            if (isEditMode && res.id == reservationId) return@any false
                                            overlaps(slot.start, slot.end, res.horaInicio, res.horaFin)
                                        }
                                        slot to occupied
                                    }
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                occupiedSlots.forEach { (slot, isOccupied) ->
                                    val isSelected = selectedStart == slot.start && selectedEnd == slot.end
                                    val isOriginal = isEditMode && originalStart == slot.start && originalEnd == slot.end

                                    val (bgColor, label, labelColor) = when {
                                        isOccupied -> Triple(Color(0xFFFF6B6B).copy(alpha = 0.35f), "OCUPADA",      Color(0xFFFF6B6B))
                                        isSelected -> Triple(Color(0xFF2DAAE1).copy(alpha = 0.35f), "SELECCIONADA", Color(0xFF2DAAE1))
                                        isOriginal -> Triple(Color(0xFFEAB308).copy(alpha = 0.35f), "ACTUAL",       Color(0xFFEAB308))
                                        else       -> Triple(Color(0xFF22C55E).copy(alpha = 0.35f), "LIBRE",        Color(0xFF22C55E))
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(55.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(bgColor)
                                            .clickable(enabled = inputsEnabled && !isOccupied) {
                                                formError = null; slotError = null
                                                selectedStart = slot.start; selectedEnd = slot.end
                                            }
                                            .padding(horizontal = 16.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(slot.label, color = Color.White,   fontWeight = FontWeight.SemiBold)
                                            Text(label,      color = labelColor,    fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    slotError?.let { Text(it, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 8.dp)) }
                    val shownGeneral = formError ?: vmError
                    if (!shownGeneral.isNullOrEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Text(shownGeneral, color = Color(0xFFFF6B6B))
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Botón guardar
                PrimaryButton(
                    text    = if (isEditMode) "Guardar cambios" else "Crear reserva",
                    enabled = inputsEnabled,
                    onClick = {
                        if (!inputsEnabled) return@PrimaryButton

                        // Limpiar errores
                        dateError = null; facilityError = null; userError = null
                        teamError = null; useTypeError  = null; slotError = null; formError = null

                        // Validaciones
                        if (selectedDate.isBlank())         dateError    = "Selecciona una fecha."
                        if (selectedFacilityName.isBlank()) facilityError = "Selecciona una instalación."
                        if (selectedUseType.isBlank())      useTypeError = "Selecciona un tipo de uso."
                        if (selectedStart == null || selectedEnd == null) slotError = "Selecciona una franja libre."

                        if (kind == ReservationKind.PERSONAL) {
                            if (isPlayer || isTrainer) selectedUserId = currentUserId
                            else if (selectedUserId == null) userError = "Selecciona un usuario."
                        } else {
                            if (selectedTeamId == null) teamError = "Selecciona un equipo."
                            if (isTrainer && selectedTeamId != null && myTeams.none { it.id == selectedTeamId })
                                teamError = "Solo puedes reservar para tus equipos."
                        }

                        if (dateError != null || facilityError != null || userError != null ||
                            teamError != null || useTypeError != null  || slotError != null) return@PrimaryButton

                        val sel = parseToLocalDate(selectedDate)
                        if (!isEditMode && sel != null && sel.isBefore(today)) {
                            dateError = "No puedes reservar en una fecha pasada."; return@PrimaryButton
                        }

                        val facilityId = facilities.firstOrNull { it.nombre == selectedFacilityName }?.id
                        if (facilityId == null) { facilityError = "Instalación no válida."; return@PrimaryButton }

                        if (!viewModel.isSlotAvailable(selectedStart!!, selectedEnd!!, reservationId)) {
                            slotError = "Esa franja ya está reservada."; return@PrimaryButton
                        }

                        val res = Reservation(
                            id              = reservationId ?: 0,
                            pistaId         = facilityId,
                            usuarioId       = if (kind == ReservationKind.PERSONAL) selectedUserId else null,
                            equipoId        = if (kind == ReservationKind.TEAM) selectedTeamId else null,
                            creadaPorUserId = currentUserId,
                            fecha           = selectedDate,
                            horaInicio      = selectedStart!!,
                            horaFin         = selectedEnd!!,
                            tipoUso         = selectedUseType.trim()
                        )

                        if (isEditMode) {
                            viewModel.updateReservation(currentUserId, roleKey, res) { ok ->
                                if (ok) navController.popBackStack()
                                else formError = viewModel.errorMessage ?: "No se ha podido actualizar"
                            }
                        } else {
                            viewModel.addReservation(currentUserId, roleKey, res) { ok ->
                                if (ok) navController.popBackStack()
                                else formError = viewModel.errorMessage ?: "No se ha podido crear"
                            }
                        }
                    }
                )

                Spacer(Modifier.height(35.dp))
            }
        }
    }
}
