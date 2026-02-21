package com.example.gesport.ui.backend.ges_team

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
import com.example.gesport.models.Sports
import com.example.gesport.models.Team
import com.example.gesport.ui.components.GeSportBackgroundScreen
import com.example.gesport.ui.components.Input
import com.example.gesport.ui.components.TeamCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GesTeamScreen(
    navController: NavHostController,
    viewModel: GesTeamViewModel
) {

    val teams = viewModel.teams
    val selectedCategory = viewModel.selectedCategory
    val searchQuery = viewModel.searchQuery
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val trainers = viewModel.trainers

    var teamToDelete by remember { mutableStateOf<Team?>(null) }

    val chipColors = FilterChipDefaults.filterChipColors(
        containerColor = Color.White.copy(alpha = 0.20f),
        labelColor = Color.White,
        selectedContainerColor = Color(0xFF2DAAE1).copy(alpha = 0.40f),
        selectedLabelColor = Color.White
    )

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
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        Text(
                            text = "Gestión de equipos",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Administra los equipos registrados en el sistema.",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // BUSCADOR
                Input(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = "Buscar por nombre de equipo",
                    leadingIconRes = R.drawable.icon_user
                )

                Spacer(Modifier.height(8.dp))

                // FILTRO POR DEPORTE (multilínea)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { viewModel.onCategorySelected(null) },
                        label = { Text("Todos") },
                        colors = chipColors,
                        border = null
                    )

                    Sports.allSports.forEach { (key, label) ->
                        FilterChip(
                            selected = selectedCategory == key,
                            onClick = {
                                val newCat = if (selectedCategory == key) null else key
                                viewModel.onCategorySelected(newCat)
                            },
                            label = { Text(label) },
                            colors = chipColors,
                            border = null
                        )
                    }
                }

                if (!errorMessage.isNullOrEmpty()) {
                    Spacer(Modifier.height(6.dp))
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
                                    text = "Cargando equipos...",
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }

                        teams.isEmpty() -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No hay equipos todavía",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Pulsa el botón + para crear uno",
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
                                    items = teams,
                                    key = { it.id }
                                ) { team ->

                                    val trainerName =
                                        trainers.firstOrNull { it.id == team.entrenadorId }?.nombre

                                    TeamCard(
                                        team = team,
                                        trainerName = trainerName,
                                        onEdit = { navController.navigate("formteam/${team.id}") },
                                        onDelete = { teamToDelete = team }
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
            onClick = { navController.navigate("formteam") },
            containerColor = Color(0xFF2DAAE1).copy(alpha = 0.40f),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 70.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir equipo")
        }

        // DIÁLOGO BORRAR
        teamToDelete?.let { team ->
            AlertDialog(
                onDismissRequest = { teamToDelete = null },
                title = { Text("Eliminar equipo", fontWeight = FontWeight.Bold) },
                text = { Text("¿Seguro que quieres eliminar a ${team.nombre}?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteTeam(team.id)
                            teamToDelete = null
                        }
                    ) {
                        Text("Eliminar", color = Color(0xFFFF6B6B))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { teamToDelete = null }) {
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