package com.example.gesport.ui.backend.ges_facility

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
import com.example.gesport.models.Facility
import com.example.gesport.models.Sports
import com.example.gesport.ui.components.GeSportBackgroundScreen
import com.example.gesport.ui.components.Input
import com.example.gesport.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddFacilityScreen(
    navController: NavHostController,
    viewModel: GesFacilityViewModel,
    facilityId: Int? = null
) {
    val isEditMode = facilityId != null

    // Campos
    var nombre by remember { mutableStateOf("") }
    var localizacion by remember { mutableStateOf("") }
    var tipoDeporte by remember { mutableStateOf("") }
    var capacidad by remember { mutableStateOf("") }
    var disponible by remember { mutableStateOf(true) }

    // Errores
    var nombreError by remember { mutableStateOf<String?>(null) }
    var localizacionError by remember { mutableStateOf<String?>(null) }
    var tipoError by remember { mutableStateOf<String?>(null) }
    var capacidadError by remember { mutableStateOf<String?>(null) }
    var formError by remember { mutableStateOf<String?>(null) }

    val vmError = viewModel.errorMessage

    val primaryBlue = Color(0xFF2DAAE1).copy(alpha = 0.40f)
    val scrollState = rememberScrollState()

    // Cargar datos si editamos
    LaunchedEffect(facilityId) {
        if (facilityId != null) {
            viewModel.loadFacilityById(facilityId) { facility ->
                if (facility != null) {
                    nombre = facility.nombre
                    localizacion = facility.localizacion.orEmpty()
                    tipoDeporte = facility.tipoDeporte
                    capacidad = facility.capacidad?.toString() ?: ""
                    disponible = facility.disponible
                } else {
                    formError = "No se ha encontrado la instalación"
                }
            }
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
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = if (isEditMode) "Editar instalación" else "Nueva instalación",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (isEditMode)
                                "Actualiza la información de la instalación."
                            else
                                "Completa los datos para registrar una nueva instalación.",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // FORM (scrolleable)
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
                            placeholder = "Nombre de la pista (ej: Pista 1)",
                            leadingIconRes = R.drawable.icon_user
                        )
                        nombreError?.let {
                            Text(text = it, color = Color(0xFFFF6B6B))
                        }

                        // Localización
                        Text(
                            text = "Localización",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Input(
                            value = localizacion,
                            onValueChange = {
                                localizacion = it
                                localizacionError = null
                                formError = null
                            },
                            placeholder = "Ubicación (ej: Pabellón A)",
                            leadingIconRes = R.drawable.icon_location
                        )
                        localizacionError?.let {
                            Text(text = it, color = Color(0xFFFF6B6B))
                        }

                        // Deporte
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
                                    selected = tipoDeporte == sportKey,
                                    onClick = {
                                        tipoDeporte = sportKey
                                        tipoError = null
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

                        tipoError?.let {
                            Text(text = it, color = Color(0xFFFF6B6B))
                        }

                        // Capacidad
                        Text(
                            text = "Capacidad",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Input(
                            value = capacidad,
                            onValueChange = {
                                capacidad = it.filter { c -> c.isDigit() }
                                capacidadError = null
                                formError = null
                            },
                            placeholder = "Capacidad (opcional)",
                            leadingIconRes = R.drawable.icon_user
                        )
                        capacidadError?.let {
                            Text(text = it, color = Color(0xFFFF6B6B))
                        }

                        // Disponible
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Disponible",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (disponible) "La instalación se puede reservar"
                                    else "No se puede reservar",
                                    color = Color.White.copy(alpha = 0.65f),
                                    fontSize = 13.sp
                                )
                            }
                            Switch(
                                checked = disponible,
                                onCheckedChange = { disponible = it },
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = primaryBlue,
                                    checkedThumbColor = Color.White,
                                    uncheckedTrackColor = Color.White.copy(alpha = 0.25f),
                                    uncheckedThumbColor = Color.White.copy(alpha = 0.85f)
                                )
                            )
                        }

                        // Errores globales
                        if (!vmError.isNullOrEmpty()) {
                            Text(text = vmError, color = Color(0xFFFF6B6B))
                        }
                        if (!formError.isNullOrEmpty()) {
                            Text(text = formError ?: "", color = Color(0xFFFF6B6B))
                        }

                        // Un pelín de aire para que el último campo no quede pegado al botón
                        Spacer(Modifier.height(12.dp))
                    }
                }

                // Botón "Guardar cambios / Crear instalación"
                Spacer(Modifier.height(10.dp))

                PrimaryButton(
                    text = if (isEditMode) "Guardar cambios" else "Crear instalación",
                    onClick = {
                        // Reset errores
                        nombreError = null
                        localizacionError = null
                        tipoError = null
                        capacidadError = null
                        formError = null

                        val n = nombre.trim()
                        val loc = localizacion.trim()
                        val t = tipoDeporte.trim()

                        if (n.isEmpty()) nombreError = "El nombre es obligatorio"
                        if (loc.isEmpty()) localizacionError = "La localización es obligatoria"
                        if (t.isEmpty()) tipoError = "Selecciona un deporte"

                        val capValue = if (capacidad.trim().isEmpty()) null else capacidad.toIntOrNull()
                        if (capacidad.isNotEmpty() && capValue == null) {
                            capacidadError = "Capacidad no válida"
                        }

                        val isValid = nombreError == null &&
                                localizacionError == null &&
                                tipoError == null &&
                                capacidadError == null
                        if (!isValid) return@PrimaryButton

                        val facility = Facility(
                            id = facilityId ?: 0,
                            nombre = n,
                            localizacion = loc,
                            tipoDeporte = t,
                            disponible = disponible,
                            capacidad = capValue
                        )

                        if (isEditMode) viewModel.updateFacility(facility)
                        else viewModel.addFacility(facility)

                        navController.popBackStack()
                    }
                )

                Spacer(Modifier.height(35.dp))
            }
        }
    }
}