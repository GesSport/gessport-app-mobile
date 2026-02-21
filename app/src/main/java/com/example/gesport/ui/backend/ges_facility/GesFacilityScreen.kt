package com.example.gesport.ui.backend.ges_facility

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.gesport.ui.components.FacilityCard
import com.example.gesport.ui.components.GeSportBackgroundScreen
import com.example.gesport.ui.components.Input

/**
 * Pantalla de gestión de instalaciones (listado).
 *
 * Permite:
 * - Buscar instalaciones por nombre.
 * - Filtrar por tipo de deporte.
 * - Mostrar estados de carga y lista vacía.
 * - Navegar al formulario de creación/edición.
 * - Eliminar instalaciones con confirmación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesFacilityScreen(
    navController: NavHostController,
    viewModel: GesFacilityViewModel
) {
    // Estados expuestos por el ViewModel
    val facilities = viewModel.facilities
    val selectedSport = viewModel.selectedSport
    val searchQuery = viewModel.searchQuery
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    // Instalación pendiente de borrar (para diálogo)
    var facilityToDelete by remember { mutableStateOf<Facility?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {

        // Fondo común reutilizable
        GeSportBackgroundScreen {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {

                // Header título + subtítulo (igual que usuarios)
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
                            text = "Gestión de instalaciones",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Administra las pistas e instalaciones del centro.",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Input Buscar (tu componente)
                Input(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = "Buscar por nombre",
                    leadingIconRes = R.drawable.icon_user // si tienes icono de pista/mapa lo cambiamos
                )

                Spacer(Modifier.height(4.dp))

                // Chips filtro por deporte
                Column(
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Filtros línea 1
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilterChip(
                            selected = selectedSport == null,
                            onClick = { viewModel.onSportSelected(null) },
                            label = { Text("Todos") },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White.copy(alpha = 0.20f),
                                labelColor = Color.White,
                                selectedContainerColor = Color(0xFF2DAAE1).copy(alpha = 0.40f),
                                selectedLabelColor = Color.White
                            ),
                            border = null
                        )

                        FilterChip(
                            selected = selectedSport == "ATLETISMO",
                            onClick = {
                                val newSport = if (selectedSport == "ATLETISMO") null else "ATLETISMO"
                                viewModel.onSportSelected(newSport)
                            },
                            label = { Text("Atletismo") },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White.copy(alpha = 0.20f),
                                labelColor = Color.White,
                                selectedContainerColor = Color(0xFF2DAAE1).copy(alpha = 0.40f),
                                selectedLabelColor = Color.White
                            ),
                            border = null
                        )

                        FilterChip(
                            selected = selectedSport == "FUTBOL",
                            onClick = {
                                val newSport = if (selectedSport == "FUTBOL") null else "FUTBOL"
                                viewModel.onSportSelected(newSport)
                            },
                            label = { Text("Fútbol") },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White.copy(alpha = 0.20f),
                                labelColor = Color.White,
                                selectedContainerColor = Color(0xFF2DAAE1).copy(alpha = 0.40f),
                                selectedLabelColor = Color.White
                            ),
                            border = null
                        )
                    }

                    // Filtros línea 2
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilterChip(
                            selected = selectedSport == "BALONCESTO",
                            onClick = {
                                val newSport = if (selectedSport == "BALONCESTO") null else "BALONCESTO"
                                viewModel.onSportSelected(newSport)
                            },
                            label = { Text("Baloncesto") },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White.copy(alpha = 0.20f),
                                labelColor = Color.White,
                                selectedContainerColor = Color(0xFF2DAAE1).copy(alpha = 0.40f),
                                selectedLabelColor = Color.White
                            ),
                            border = null
                        )

                        FilterChip(
                            selected = selectedSport == "PADEL",
                            onClick = {
                                val newSport = if (selectedSport == "PADEL") null else "PADEL"
                                viewModel.onSportSelected(newSport)
                            },
                            label = { Text("Pádel") },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White.copy(alpha = 0.20f),
                                labelColor = Color.White,
                                selectedContainerColor = Color(0xFF2DAAE1).copy(alpha = 0.40f),
                                selectedLabelColor = Color.White
                            ),
                            border = null
                        )

                        FilterChip(
                            selected = selectedSport == "NATACION",
                            onClick = {
                                val newSport = if (selectedSport == "NATACION") null else "NATACION"
                                viewModel.onSportSelected(newSport)
                            },
                            label = { Text("Natación") },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White.copy(alpha = 0.20f),
                                labelColor = Color.White,
                                selectedContainerColor = Color(0xFF2DAAE1).copy(alpha = 0.40f),
                                selectedLabelColor = Color.White
                            ),
                            border = null
                        )

                        FilterChip(
                            selected = selectedSport == "TENIS",
                            onClick = {
                                val newSport = if (selectedSport == "TENIS") null else "TENIS"
                                viewModel.onSportSelected(newSport)
                            },
                            label = { Text("Tenis") },
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

                // Error (si existe)
                if (!errorMessage.isNullOrEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = errorMessage,
                        color = Color(0xFFFF6B6B)
                    )
                }

                // Contenido principal
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when {
                        // Loading
                        isLoading -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = Color(0xFF2DAAE1))
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Cargando instalaciones...",
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }

                        // Vacío
                        facilities.isEmpty() -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No hay instalaciones todavía",
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

                        // Lista
                        else -> {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .offset(y = (10).dp)
                            ) {
                                items(
                                    items = facilities,
                                    key = { it.id }
                                ) { facility ->
                                    FacilityCard(
                                        facility = facility,
                                        onEdit = {
                                            navController.navigate("formfacility/${facility.id}")
                                        },
                                        onDelete = {
                                            facilityToDelete = facility
                                        }
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
            onClick = { navController.navigate("formfacility") },
            containerColor = Color(0xFF2DAAE1).copy(alpha = 0.40f),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 70.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Añadir instalación"
            )
        }

        // Diálogo confirmar borrado (igual que usuarios)
        facilityToDelete?.let { facility ->
            AlertDialog(
                onDismissRequest = { facilityToDelete = null },
                title = {
                    Text(
                        text = "Eliminar instalación",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text("¿Seguro que quieres eliminar ${facility.nombre}?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteFacility(facility.id)
                            facilityToDelete = null
                        }
                    ) {
                        Text("Eliminar", color = Color(0xFFFF6B6B))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { facilityToDelete = null }) {
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
