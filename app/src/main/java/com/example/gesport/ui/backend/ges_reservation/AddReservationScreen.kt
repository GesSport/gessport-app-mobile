package com.example.gesport.ui.backend.ges_reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
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
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern(p))
        } catch (_: DateTimeParseException) { }
    }
    return null
}

@Composable
fun AddReservationScreen(
    navController: NavHostController,
    viewModel: GesReservationViewModel,
    reservationId: Int? = null
) {
    val isEditMode = reservationId != null

    val isLoading = viewModel.isLoading
    val vmError = viewModel.errorMessage

    val facilities = viewModel.facilities
    val users = viewModel.users
    val reservations = viewModel.reservations // grid: fecha+pista

    // --------- Form state ----------
    var selectedDate by remember { mutableStateOf("") } // SIEMPRE yyyy-MM-dd
    var selectedFacilityName by remember { mutableStateOf("") }

    var selectedUserLabel by remember { mutableStateOf("") }
    var selectedUserId by remember { mutableStateOf<Int?>(null) }

    var selectedUseType by remember { mutableStateOf("") }

    var selectedStart by remember { mutableStateOf<String?>(null) }
    var selectedEnd by remember { mutableStateOf<String?>(null) }

    // Para “modo editar”: guardar la franja original y pintarla diferente
    var originalStart by remember { mutableStateOf<String?>(null) }
    var originalEnd by remember { mutableStateOf<String?>(null) }

    // ✅ Para permitir visualizar reservas antiguas en edición (sin romper reglas de creación)
    var originalDate by remember { mutableStateOf<String?>(null) } // yyyy-MM-dd

    // Errores por campo
    var dateError by remember { mutableStateOf<String?>(null) }
    var facilityError by remember { mutableStateOf<String?>(null) }
    var userError by remember { mutableStateOf<String?>(null) }
    var useTypeError by remember { mutableStateOf<String?>(null) }
    var slotError by remember { mutableStateOf<String?>(null) }

    // Error general (fallos de VM / etc.)
    var formError by remember { mutableStateOf<String?>(null) }

    val facilityOptions = remember(facilities) { facilities.map { it.nombre } }
    val userOptions = remember(users) { users.map { "${it.nombre} · ${it.email}" } }

    val useTypeOptions = remember {
        listOf("Entrenamiento", "Partido", "Clase", "Torneo", "Libre")
    }

    // Formato canónico para BD/UI
    val outFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val today = remember { LocalDate.now(ZoneId.of("Europe/Madrid")) }

    // Preselección de instalación (si VM trae una)
    LaunchedEffect(facilities, viewModel.selectedFacilityId) {
        val id = viewModel.selectedFacilityId
        if (id != null && selectedFacilityName.isBlank()) {
            selectedFacilityName = facilities.firstOrNull { it.id == id }?.nombre.orEmpty()
        }
    }

    // Cargar datos al editar
    LaunchedEffect(reservationId) {
        if (reservationId == null) return@LaunchedEffect

        viewModel.loadReservationById(reservationId) { res ->
            if (res == null) {
                formError = "No se ha encontrado la reserva"
                return@loadReservationById
            }

            selectedDate = res.fecha
            originalDate = res.fecha

            val facName = viewModel.getFacilityNameById(res.pistaId).orEmpty()
            selectedFacilityName = facName

            val u = viewModel.getUserById(res.usuarioId)
            selectedUserId = u?.id
            selectedUserLabel = if (u != null) "${u.nombre} · ${u.email}" else ""

            selectedUseType = res.tipoUso.orEmpty()

            selectedStart = res.horaInicio
            selectedEnd = res.horaFin
            originalStart = res.horaInicio
            originalEnd = res.horaFin

            viewModel.onDateSelected(res.fecha)
            viewModel.onFacilitySelected(res.pistaId)
        }
    }

    // ✅ Determina si la fecha seleccionada es pasada (por si estamos editando una reserva pasada)
    val selectedLocalDate = remember(selectedDate) { parseToLocalDate(selectedDate) }
    val isSelectedPast = selectedLocalDate?.isBefore(today) == true

    Box(modifier = Modifier.fillMaxSize()) {
        GeSportBackgroundScreen {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {

                // ================= BLOQUE SCROLLEABLE =================
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 90.dp),
                    verticalArrangement = Arrangement.Top
                ) {

                    // HEADER
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        Column(modifier = Modifier.align(Alignment.BottomStart)) {
                            Text(
                                text = if (isEditMode) "Editar reserva" else "Nueva reserva",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = if (isEditMode)
                                    "Modifica los datos de la reserva."
                                else
                                    "Selecciona fecha, pista, usuario y franja horaria.",
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // ====== FECHA ======
                    Text(
                        text = "Fecha",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    DatePickerField(
                        value = selectedDate,
                        onDateSelected = { pickedStr ->
                            formError = null
                            dateError = null

                            val picked = parseToLocalDate(pickedStr)
                            if (picked == null) {
                                selectedDate = ""
                                dateError = "Formato de fecha no válido."
                                return@DatePickerField
                            }

                            // Normalizamos SIEMPRE a yyyy-MM-dd
                            val pickedCanonical = picked.format(outFormatter)

                            // ✅ Bloqueo fechas pasadas:
                            // - Crear: nunca permitimos pasado
                            // - Editar: permitimos quedarnos con la fecha original (aunque sea pasada),
                            //          pero NO cambiar a otra fecha pasada.
                            val tryingPast = picked.isBefore(today)
                            val isKeepingOriginal = isEditMode && originalDate == pickedCanonical

                            if (tryingPast && !isKeepingOriginal) {
                                dateError = "No puedes reservar en una fecha pasada."
                                return@DatePickerField
                            }

                            // OK
                            selectedDate = pickedCanonical

                            selectedStart = null
                            selectedEnd = null
                            slotError = null

                            if (!isEditMode) {
                                originalStart = null
                                originalEnd = null
                                originalDate = null
                            }

                            viewModel.onDateSelected(pickedCanonical)
                        },
                        placeholder = "Selecciona una fecha",
                        leadingIconRes = R.drawable.icon_reservation
                    )

                    dateError?.let {
                        Text(
                            text = it,
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }

                    // Si estamos editando una reserva pasada, avisamos (queda muy “pro”)
                    if (isEditMode && isSelectedPast) {
                        Text(
                            text = "Esta reserva es de una fecha pasada. Puedes revisarla, pero no crear reservas en fechas anteriores.",
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // ====== INSTALACIÓN ======
                    Text(
                        text = "Instalación",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    SelectField(
                        value = selectedFacilityName,
                        onSelected = { pickedName ->
                            selectedFacilityName = pickedName
                            formError = null
                            facilityError = null

                            selectedStart = null
                            selectedEnd = null
                            slotError = null

                            if (!isEditMode) {
                                originalStart = null
                                originalEnd = null
                            }

                            val pickedId = facilities.firstOrNull { it.nombre == pickedName }?.id
                            viewModel.onFacilitySelected(pickedId)
                        },
                        placeholder = "Selecciona una pista",
                        leadingIconRes = R.drawable.icon_location,
                        options = facilityOptions
                    )

                    facilityError?.let {
                        Text(
                            text = it,
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // ====== USUARIO ======
                    Text(
                        text = "Usuario",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    SelectField(
                        value = selectedUserLabel,
                        onSelected = { picked ->
                            selectedUserLabel = picked
                            formError = null
                            userError = null

                            val email = picked.substringAfter("·").trim()
                            val u = users.firstOrNull { it.email.trim().equals(email, ignoreCase = true) }
                            selectedUserId = u?.id
                        },
                        placeholder = "Selecciona un usuario",
                        leadingIconRes = R.drawable.icon_user,
                        options = userOptions
                    )

                    userError?.let {
                        Text(
                            text = it,
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // ====== TIPO DE USO ======
                    Text(
                        text = "Tipo de uso",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    SelectField(
                        value = selectedUseType,
                        onSelected = {
                            selectedUseType = it
                            formError = null
                            useTypeError = null
                        },
                        placeholder = "Selecciona el tipo de uso",
                        leadingIconRes = R.drawable.icon_reservation,
                        options = useTypeOptions
                    )

                    useTypeError?.let {
                        Text(
                            text = it,
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // ====== GRID ======
                    Text(
                        text = "Franja horaria",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    if (isLoading) {
                        Text(
                            text = "Cargando reservas...",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    } else if (selectedDate.isBlank() || selectedFacilityName.isBlank()) {
                        Text(
                            text = "Selecciona fecha y pista para ver las franjas",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    } else if (!isEditMode && isSelectedPast) {
                        // Extra safety (en teoría nunca pasa porque bloqueamos al seleccionar)
                        Text(
                            text = "No puedes reservar en fechas pasadas.",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    } else {
                        val slots = remember {
                            generateSlots(
                                dayStart = "08:00",
                                dayEnd = "22:00",
                                slotMinutes = 60L
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            slots.forEach { slot ->
                                val isReservedByOther = reservations.any { res ->
                                    if (isEditMode && res.id == reservationId) return@any false
                                    overlaps(
                                        slotStart = slot.start,
                                        slotEnd = slot.end,
                                        resStart = res.horaInicio,
                                        resEnd = res.horaFin
                                    )
                                }

                                val isSelected = selectedStart == slot.start && selectedEnd == slot.end
                                val isOriginal = isEditMode &&
                                        originalStart == slot.start &&
                                        originalEnd == slot.end

                                val (bgColor, label, labelColor) = when {
                                    isReservedByOther -> Triple(
                                        Color(0xFFFF6B6B).copy(alpha = 0.35f),
                                        "OCUPADA",
                                        Color(0xFFFF6B6B)
                                    )
                                    isSelected -> Triple(
                                        Color(0xFF2DAAE1).copy(alpha = 0.35f),
                                        "SELECCIONADA",
                                        Color(0xFF2DAAE1)
                                    )
                                    isOriginal -> Triple(
                                        Color(0xFFEAB308).copy(alpha = 0.35f),
                                        "ACTUAL",
                                        Color(0xFFEAB308)
                                    )
                                    else -> Triple(
                                        Color(0xFF22C55E).copy(alpha = 0.35f),
                                        "LIBRE",
                                        Color(0xFF22C55E)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(55.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(bgColor)
                                        .clickable(enabled = !isReservedByOther) {
                                            formError = null
                                            slotError = null
                                            selectedStart = slot.start
                                            selectedEnd = slot.end
                                        }
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = slot.label,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = label,
                                            color = labelColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    slotError?.let {
                        Text(
                            text = it,
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    val shownGeneral = formError ?: vmError
                    if (!shownGeneral.isNullOrEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Text(text = shownGeneral, color = Color(0xFFFF6B6B))
                    }

                    Spacer(Modifier.height(16.dp))
                }

                // ================= BOTÓN FIJO =================
                PrimaryButton(
                    text = if (isEditMode) "Guardar cambios" else "Crear reserva",
                    onClick = {
                        dateError = null
                        facilityError = null
                        userError = null
                        useTypeError = null
                        slotError = null
                        formError = null

                        if (selectedDate.isBlank()) dateError = "Selecciona una fecha."
                        if (selectedFacilityName.isBlank()) facilityError = "Selecciona una instalación."
                        if (selectedUserId == null) userError = "Selecciona un usuario."
                        if (selectedUseType.isBlank()) useTypeError = "Selecciona un tipo de uso."
                        if (selectedStart == null || selectedEnd == null) slotError = "Selecciona una franja libre."

                        val isValid = dateError == null &&
                                facilityError == null &&
                                userError == null &&
                                useTypeError == null &&
                                slotError == null

                        if (!isValid) return@PrimaryButton

                        // Safety extra: crear nunca en pasado
                        val sel = parseToLocalDate(selectedDate)
                        if (!isEditMode && sel != null && sel.isBefore(today)) {
                            dateError = "No puedes reservar en una fecha pasada."
                            return@PrimaryButton
                        }

                        val facilityId = facilities.firstOrNull { it.nombre == selectedFacilityName }?.id
                        if (facilityId == null) {
                            facilityError = "Selecciona una instalación válida."
                            return@PrimaryButton
                        }

                        val available = viewModel.isSlotAvailable(
                            start = selectedStart!!,
                            end = selectedEnd!!,
                            ignoreReservationId = reservationId
                        )
                        if (!available) {
                            slotError = "Esa franja ya está reservada."
                            return@PrimaryButton
                        }

                        val res = Reservation(
                            id = reservationId ?: 0,
                            pistaId = facilityId,
                            usuarioId = selectedUserId!!,
                            fecha = selectedDate,
                            horaInicio = selectedStart!!,
                            horaFin = selectedEnd!!,
                            tipoUso = selectedUseType.trim()
                        )

                        if (isEditMode) {
                            viewModel.updateReservation(res) { ok ->
                                if (ok) navController.popBackStack()
                                else formError = viewModel.errorMessage ?: "No se ha podido actualizar"
                            }
                        } else {
                            viewModel.addReservation(res) { ok ->
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