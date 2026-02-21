package com.example.gesport.ui.backend.ges_reservation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.gesport.models.Reservation
import com.example.gesport.ui.components.GeSportBackgroundScreen
import com.example.gesport.ui.components.Input
import com.example.gesport.ui.components.ReservationCard
import java.time.LocalDate
import java.time.ZoneId

private enum class ReservationTimeFilter { ALL, PAST, TODAY, FUTURE }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GesReservationScreen(
    navController: NavHostController,
    viewModel: GesReservationViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadAllReservations()
    }

    val reservations = viewModel.allReservations
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    var searchQuery by remember { mutableStateOf("") }

    // ✅ Chips: Todas / Pasadas / Hoy / Futuras
    var timeFilter by remember { mutableStateOf(ReservationTimeFilter.FUTURE) }

    var reservationToDelete by remember { mutableStateOf<Reservation?>(null) }

    val chipColors = FilterChipDefaults.filterChipColors(
        containerColor = Color.White.copy(alpha = 0.20f),
        labelColor = Color.White,
        selectedContainerColor = Color(0xFF2DAAE1).copy(alpha = 0.40f),
        selectedLabelColor = Color.White
    )

    val today = remember {
        LocalDate.now(ZoneId.of("Europe/Madrid"))
    }

    fun matchesTimeFilter(res: Reservation): Boolean {
        if (timeFilter == ReservationTimeFilter.ALL) return true

        val d = runCatching { LocalDate.parse(res.fecha) }.getOrNull()
            ?: return false // si no parsea, no lo metemos en pasadas/hoy/futuras

        return when (timeFilter) {
            ReservationTimeFilter.PAST -> d.isBefore(today)
            ReservationTimeFilter.TODAY -> d.isEqual(today)
            ReservationTimeFilter.FUTURE -> d.isAfter(today)
            ReservationTimeFilter.ALL -> true
        }
    }

    val filtered by remember(reservations, searchQuery, viewModel.users, timeFilter) {
        derivedStateOf {
            val q = searchQuery.trim().lowercase()

            reservations
                .asSequence()
                .filter { matchesTimeFilter(it) }
                .filter { res ->
                    if (q.isEmpty()) return@filter true

                    val facilityName = viewModel.getFacilityNameById(res.pistaId).orEmpty().lowercase()
                    val userName = viewModel.getUserNameById(res.usuarioId).orEmpty().lowercase()

                    val userEmail = runCatching { viewModel.getUserEmailById(res.usuarioId) }
                        .getOrNull().orEmpty().lowercase()

                    val userRole = runCatching { viewModel.getUserById(res.usuarioId)?.rol }
                        .getOrNull().orEmpty().lowercase()

                    facilityName.contains(q) ||
                            userName.contains(q) ||
                            userEmail.contains(q) ||
                            userRole.contains(q) ||
                            res.fecha.lowercase().contains(q) ||
                            res.horaInicio.lowercase().contains(q) ||
                            res.horaFin.lowercase().contains(q) ||
                            (res.tipoUso?.lowercase()?.contains(q) == true)
                }
                .sortedWith(compareBy<Reservation>({ it.fecha }, { it.horaInicio }, { it.horaFin }))
                .toList()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GeSportBackgroundScreen {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.BottomStart)) {
                        Text(
                            text = "Gestión de reservas",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Administra las reservas registradas en el sistema.",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ✅ CHIPS TEMPORALES (TODAS / PASADAS / HOY / FUTURAS)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = timeFilter == ReservationTimeFilter.ALL,
                        onClick = { timeFilter = ReservationTimeFilter.ALL },
                        label = { Text("Todas") },
                        colors = chipColors,
                        border = null
                    )
                    FilterChip(
                        selected = timeFilter == ReservationTimeFilter.PAST,
                        onClick = { timeFilter = ReservationTimeFilter.PAST },
                        label = { Text("Pasadas") },
                        colors = chipColors,
                        border = null
                    )
                    FilterChip(
                        selected = timeFilter == ReservationTimeFilter.TODAY,
                        onClick = { timeFilter = ReservationTimeFilter.TODAY },
                        label = { Text("Hoy") },
                        colors = chipColors,
                        border = null
                    )
                    FilterChip(
                        selected = timeFilter == ReservationTimeFilter.FUTURE,
                        onClick = { timeFilter = ReservationTimeFilter.FUTURE },
                        label = { Text("Futuras") },
                        colors = chipColors,
                        border = null
                    )
                }

                Spacer(Modifier.height(10.dp))

                // BUSCADOR
                Input(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Buscar por pista, usuario, fecha...",
                    leadingIconRes = R.drawable.icon_user
                )

                if (!errorMessage.isNullOrEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(text = errorMessage, color = Color(0xFFFF6B6B))
                }

                Spacer(Modifier.height(12.dp))

                // CONTENIDO
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when {
                        isLoading -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = Color(0xFF2DAAE1))
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Cargando reservas...",
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }

                        filtered.isEmpty() -> {
                            val emptyText = when (timeFilter) {
                                ReservationTimeFilter.ALL -> "No hay reservas todavía"
                                ReservationTimeFilter.PAST -> "No hay reservas pasadas"
                                ReservationTimeFilter.TODAY -> "No hay reservas para hoy"
                                ReservationTimeFilter.FUTURE -> "No hay reservas futuras"
                            }

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = emptyText,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Pulsa el botón + para crear una",
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }

                        else -> {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = filtered,
                                    key = { it.id }
                                ) { res ->
                                    ReservationCard(
                                        reservation = res,
                                        facility = runCatching { viewModel.getFacilityById(res.pistaId) }.getOrNull(),
                                        user = runCatching { viewModel.getUserById(res.usuarioId) }.getOrNull(),
                                        onEdit = { navController.navigate("formreservation/${res.id}") },
                                        onDelete = { reservationToDelete = res }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { navController.navigate("formreservation") },
            containerColor = Color(0xFF2DAAE1).copy(alpha = 0.40f),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 70.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir reserva")
        }

        // DIÁLOGO BORRAR
        reservationToDelete?.let { res ->
            val facilityName = viewModel.getFacilityNameById(res.pistaId) ?: "Pista #${res.pistaId}"
            AlertDialog(
                onDismissRequest = { reservationToDelete = null },
                title = { Text("Eliminar reserva", fontWeight = FontWeight.Bold) },
                text = {
                    Text("¿Seguro que quieres eliminar la reserva en $facilityName (${res.fecha} · ${res.horaInicio}-${res.horaFin})?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteReservation(res.id)
                            reservationToDelete = null
                        }
                    ) {
                        Text("Eliminar", color = Color(0xFFFF6B6B))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { reservationToDelete = null }) {
                        Text("Cancelar")
                    }
                },
                containerColor = Color.Black,
                titleContentColor = Color.White,
                textContentColor = Color.White
            )
        }
    }
}