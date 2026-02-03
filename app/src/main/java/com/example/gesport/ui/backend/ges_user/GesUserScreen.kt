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

/**
 * Pantalla de gestión de usuarios (listado).
 *
 * Permite:
 * - Buscar usuarios por nombre o email.
 * - Filtrar por rol.
 * - Mostrar estados de carga y de lista vacía.
 * - Navegar al formulario de creación/edición.
 * - Eliminar usuarios con confirmación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesUserScreen(
    navController: NavHostController,
    viewModel: GesUserViewModel
) {
    // Estados expuestos por el ViewModel
    val users = viewModel.users
    val selectedRole = viewModel.selectedRole
    val searchQuery = viewModel.searchQuery
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    // Usuario pendiente de borrar (antes de mostrar el diálogo de confirmación)
    var userToDelete by remember { mutableStateOf<User?>(null) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fondo común reutilizable con imagen y capa oscura
        GeSportBackgroundScreen {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {

                // Titulo + subtítulo
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

                // Input Buscar
                Input(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = "Buscar por nombre o email",
                    leadingIconRes = R.drawable.icon_user
                )

                Spacer(Modifier.height(4.dp))

                // Filtros por rol
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Chip para mostrar todos los usuarios (sin filtro de rol)
                    FilterChip(
                        selected = selectedRole == null,
                        onClick = { viewModel.onRoleSelected(null) },
                        label = { Text("Todos") },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White.copy(alpha = 0.20f),
                            labelColor = Color.White,
                            selectedContainerColor = Color(0xFF2DAAE1).copy(alpha = 0.40f),
                            selectedLabelColor = Color.White
                        ),
                        border = null
                    )

                    // Chips dinámicos para cada rol definido en UserRoles
                    UserRoles.allRoles.forEach { (roleKey, roleLabel) ->
                        FilterChip(
                            selected = selectedRole == roleKey,
                            onClick = {
                                // Si se vuelve a pulsar el mismo rol, se deselecciona (vuelve a null)
                                val newRole = if (selectedRole == roleKey) null else roleKey
                                viewModel.onRoleSelected(newRole)
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

                // Mensaje de error del ViewModel (si existe)
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
                        // Estado de carga
                        isLoading -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
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

                        // Estado lista vacía
                        users.isEmpty() -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
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

                        // Lista con usuarios
                        else -> {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .offset(y = (-80).dp) // desplaza la lista hacia arriba para compensar el diseño
                            ) {
                                items(
                                    items = users,
                                    key = { user -> user.id }
                                ) { user ->
                                    UserCard(
                                        user = user,
                                        onEdit = {
                                            // Navega al formulario en modo edición con el id del usuario
                                            navController.navigate("formuser/${user.id}")
                                        },
                                        onDelete = {
                                            // Abre el diálogo de confirmación de borrado para este usuario
                                            userToDelete = user
                                        }
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }

        // Botón ( + ) añadir
        FloatingActionButton(
            onClick = { navController.navigate("formuser") },
            containerColor = Color(0xFF2DAAE1).copy(alpha = 0.40f),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 70.dp)  // Para que no quede pegado al borde inferior
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Añadir usuario"
            )
        }

        // Diálogo de confirmación de borrado
        userToDelete?.let { user ->
            AlertDialog(
                onDismissRequest = { userToDelete = null },
                title = {
                    Text(
                        text = "Eliminar usuario",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text("¿Seguro que quieres eliminar a ${user.nombre}?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Llama al ViewModel para borrar el usuario y cierra el diálogo
                            viewModel.deleteUser(user.id)
                            userToDelete = null
                        }
                    ) {
                        Text("Eliminar", color = Color(0xFFFF6B6B))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { userToDelete = null }
                    ) {
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
