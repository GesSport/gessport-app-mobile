package com.example.gesport.ui.backend.ges_team

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.example.gesport.models.Sports
import com.example.gesport.models.Team
import com.example.gesport.ui.components.GeSportBackgroundScreen
import com.example.gesport.ui.components.Input
import com.example.gesport.ui.components.PrimaryButton
import com.example.gesport.ui.components.SelectField

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTeamScreen(
    navController: NavHostController,
    viewModel: GesTeamViewModel,
    teamId: Int? = null
) {
    val isEditMode = teamId != null

    // Campos
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") } // key de Sports (TENIS/PADEL/...)
    var entrenadorId by remember { mutableStateOf<Int?>(null) }

    // Errores
    var nombreError by remember { mutableStateOf<String?>(null) }
    var categoriaError by remember { mutableStateOf<String?>(null) }
    var formError by remember { mutableStateOf<String?>(null) }

    val vmError = viewModel.errorMessage
    val primaryBlue = Color(0xFF2DAAE1).copy(alpha = 0.40f)
    val scrollState = rememberScrollState()

    // Entrenadores (para selector)
    val trainers = viewModel.trainers  // <-- lo ajustamos si aún no existe en tu VM

    // Cargar datos si editamos
    LaunchedEffect(teamId) {
        if (teamId != null) {
            viewModel.loadTeamById(teamId) { team ->
                if (team != null) {
                    nombre = team.nombre
                    categoria = team.categoria
                    entrenadorId = team.entrenadorId
                } else {
                    formError = "No se ha encontrado el equipo"
                }
            }
        }
    }

    // Selector: opciones = ids en string (SelectField es genérico)
    val trainerOptions = remember(trainers) { trainers.map { it.id.toString() } }
    val selectedTrainerLabel = remember(trainers, entrenadorId) {
        trainers.firstOrNull { it.id == entrenadorId }?.nombre.orEmpty()
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
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = if (isEditMode) "Editar equipo" else "Nuevo equipo",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (isEditMode)
                                "Actualiza la información del equipo."
                            else
                                "Completa los datos para registrar un nuevo equipo.",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // FORM scrolleable
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // Nombre
                        Text(
                            text = "Nombre",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Input(
                            value = nombre,
                            onValueChange = {
                                nombre = it
                                nombreError = null
                                formError = null
                            },
                            placeholder = "Nombre del equipo",
                            leadingIconRes = R.drawable.icon_user
                        )
                        nombreError?.let { Text(text = it, color = Color(0xFFFF6B6B)) }

                        // Deporte (categoría)
                        Text(
                            text = "Deporte",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Sports.allSports.forEach { (sportKey, sportLabel) ->
                                FilterChip(
                                    selected = categoria == sportKey,
                                    onClick = {
                                        categoria = sportKey
                                        categoriaError = null
                                        formError = null
                                    },
                                    label = { Text(sportLabel) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = Color.White.copy(alpha = 0.20f),
                                        labelColor = Color.White,
                                        selectedContainerColor = primaryBlue,
                                        selectedLabelColor = Color.White
                                    ),
                                    border = null
                                )
                            }
                        }

                        categoriaError?.let { Text(text = it, color = Color(0xFFFF6B6B)) }

                        // Entrenador (opcional)
                        Text(
                            text = "Entrenador (opcional)",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        if (trainerOptions.isEmpty()) {
                            Text(
                                text = "No hay entrenadores registrados",
                                color = Color.White.copy(alpha = 0.65f)
                            )
                        } else {
                            SelectField(
                                value = selectedTrainerLabel,
                                onSelected = { idStr ->
                                    entrenadorId = idStr.toIntOrNull()
                                    formError = null
                                },
                                placeholder = "Selecciona un entrenador",
                                leadingIconRes = R.drawable.icon_user,
                                options = trainerOptions,
                                optionLabel = { idStr ->
                                    val id = idStr.toIntOrNull()
                                    trainers.firstOrNull { it.id == id }?.nombre ?: idStr
                                }
                            )

                            // Botón pequeño para “quitar entrenador”
                            TextButton(
                                onClick = { entrenadorId = null },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "Quitar entrenador",
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            }
                        }

                        // Errores globales
                        if (!vmError.isNullOrEmpty()) {
                            Text(text = vmError, color = Color(0xFFFF6B6B))
                        }
                        if (!formError.isNullOrEmpty()) {
                            Text(text = formError ?: "", color = Color(0xFFFF6B6B))
                        }

                        Spacer(Modifier.height(12.dp))
                    }
                }

                Spacer(Modifier.height(10.dp))

                // BOTÓN FIJO
                PrimaryButton(
                    text = if (isEditMode) "Guardar cambios" else "Crear equipo",
                    onClick = {
                        nombreError = null
                        categoriaError = null
                        formError = null

                        val n = nombre.trim()
                        val c = categoria.trim()

                        if (n.isEmpty()) nombreError = "El nombre es obligatorio"
                        if (c.isEmpty()) categoriaError = "Selecciona un deporte"

                        val isValid = nombreError == null && categoriaError == null
                        if (!isValid) return@PrimaryButton

                        val team = Team(
                            id = teamId ?: 0,
                            nombre = n,
                            categoria = c,
                            entrenadorId = entrenadorId
                        )

                        if (isEditMode) viewModel.updateTeam(team)
                        else viewModel.addTeam(team)

                        navController.popBackStack()
                    }
                )

                Spacer(Modifier.height(35.dp))
            }
        }
    }
}