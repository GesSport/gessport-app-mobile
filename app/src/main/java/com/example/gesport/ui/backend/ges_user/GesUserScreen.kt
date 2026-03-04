package com.example.gesport.ui.backend.ges_user

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
import com.example.gesport.models.User
import com.example.gesport.models.UserRoles
import com.example.gesport.ui.components.GeSportBackgroundScreen
import com.example.gesport.ui.components.Input
import com.example.gesport.ui.components.UserCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesUserScreen(
    navController: NavHostController,
    viewModel: GesUserViewModel
) {
    val users = viewModel.users
    val selectedRole = viewModel.selectedRole
    val searchQuery = viewModel.searchQuery
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    var userToDelete by remember { mutableStateOf<User?>(null) }

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
                            text = "Gestión de usuarios",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Administra los usuarios registrados en el sistema.",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Buscar
                Input(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = "Buscar por nombre o email",
                    leadingIconRes = R.drawable.icon_user
                )

                Spacer(Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // Línea 1: Todos, Admin
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilterChip(
                            selected = selectedRole == null,
                            onClick = { viewModel.onRoleSelected(null) },
                            label = { Text("Todos") },
                            colors = chipColors,
                            border = null
                        )

                        val adminKey = UserRoles.ADMIN_DEPORTIVO
                        val adminLabel = UserRoles.allRoles[adminKey] ?: "Admin"

                        FilterChip(
                            selected = selectedRole == adminKey,
                            onClick = {
                                val newRole = if (selectedRole == adminKey) null else adminKey
                                viewModel.onRoleSelected(newRole)
                            },
                            label = { Text(adminLabel) },
                            colors = chipColors,
                            border = null
                        )
                    }

                    // Línea 2: Entrenador, Jugador, Árbitro
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val rolesLine2 = listOf(
                            UserRoles.ENTRENADOR,
                            UserRoles.JUGADOR,
                            UserRoles.ARBITRO
                        )

                        rolesLine2.forEach { roleKey ->
                            val roleLabel = UserRoles.allRoles[roleKey] ?: roleKey

                            FilterChip(
                                selected = selectedRole == roleKey,
                                onClick = {
                                    val newRole = if (selectedRole == roleKey) null else roleKey
                                    viewModel.onRoleSelected(newRole)
                                },
                                label = { Text(roleLabel) },
                                colors = chipColors,
                                border = null
                            )
                        }
                    }
                }

                // Error
                if (!errorMessage.isNullOrEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(text = errorMessage, color = Color(0xFFFF6B6B))
                }

                Spacer(Modifier.height(12.dp))

                // Contenido principal
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
                                    text = "Cargando usuarios...",
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }

                        users.isEmpty() -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No hay usuarios todavía",
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
                                // IMPORTANTE: quitamos offset para que NO se meta bajo los chips
                            ) {
                                items(
                                    items = users,
                                    key = { it.id }
                                ) { user ->
                                    UserCard(
                                        user = user,
                                        onEdit = { navController.navigate("formuser/${user.id}") },
                                        onDelete = { userToDelete = user }
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
            onClick = { navController.navigate("formuser") },
            containerColor = Color(0xFF2DAAE1).copy(alpha = 0.40f),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 70.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir usuario")
        }

        // Diálogo eliminar
        userToDelete?.let { user ->
            AlertDialog(
                onDismissRequest = { userToDelete = null },
                title = { Text("Eliminar usuario", fontWeight = FontWeight.Bold) },
                text = { Text("¿Seguro que quieres eliminar a ${user.nombre}?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteUser(user.id)
                            userToDelete = null
                        }
                    ) {
                        Text("Eliminar", color = Color(0xFFFF6B6B))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { userToDelete = null }) {
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