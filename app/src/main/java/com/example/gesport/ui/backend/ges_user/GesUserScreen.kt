package com.example.gesport.ui.backend.ges_user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gesport.data.DataUserRepository

@Composable
fun GesUserScreen(
    navController: NavHostController
)  {
    // 1) Le pido a Compose el contexto actual (por si lo necesitáramos)
    val context = LocalContext.current
    // 2) Creo el ViewModel indicándole una factory
    val viewModel: GesUserViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {

                // 2.1) Aquí decido cómo construir el REPOSITORIO
                val repo = DataUserRepository()

                // 2.2) Aquí decido cómo construir el VIEWMODEL
                return GesUserViewModel(repo) as T
            }
        }
    )
    val users = viewModel.users

    val selectedRole = viewModel.selectedRole

    Column {
        // Aquí irían tus chips (Administrador deportivo, Entrenador, Árbitro, Jugador)
        Row {
            // Chip "Todos"
            FilterChip(
                selected = selectedRole == null,
                onClick = { viewModel.onRoleSelected(null) },
                label = { Text("Todos") }
            )
            /*UserRoles.allRoles.forEach { role ->
                FilterChip(
                    selected = selectedRole == role,
                    onClick = { viewModel.onRoleSelected(role) },
                    label = { Text(role) }
                )
            }*/


        }

        // Listado
        LazyColumn {
            items(users) { user ->
                Text(text = "${user.name} - ${user.rol}")
            }
        }
    }
}