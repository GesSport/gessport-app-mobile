package com.example.gesport.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gesport.ui.components.DashboardTile
import com.example.gesport.ui.components.GeSportBackgroundScreen

/**
 * Pantalla de panel de administración.
 *
 * Desde aquí el usuario con rol administrativo puede acceder
 * a las distintas áreas de gestión: usuarios, equipos, pistas y reservas.
 */
@Composable
fun DashboardScreen(
    navController: NavHostController,
    username: String?
) {
    // Fondo común reutilizable con imagen y capa oscura
    GeSportBackgroundScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.Start
        ) {

            // Título + subtítulo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.BottomStart),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Panel de administración",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Gestiona usuarios, equipos, pistas y reservas desde un único lugar.",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Saludo personalizado usando el nombre recibido en la navegación
            Text(
                text = "Hola, ${username ?: "Administrador"} 👋",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            // Grid de botones (2x2)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardTile(
                        label = "Usuarios",
                        icon = Icons.Default.Person,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Navega al panel de usuarios
                        navController.navigate("gesuser")
                    }

                    DashboardTile(
                        label = "Equipos",
                        icon = Icons.Default.Build,
                        modifier = Modifier.weight(1f)
                    ) {
                        // TODO: Navegar a gestión de equipos
                        // navController.navigate("")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardTile(
                        label = "Pistas",
                        icon = Icons.Default.Build,
                        modifier = Modifier.weight(1f)
                    ) {
                        // TODO: Navegar a gestión de pistas
                        // navController.navigate("")
                    }

                    DashboardTile(
                        label = "Reservas",
                        icon = Icons.Default.Build,
                        modifier = Modifier.weight(1f)
                    ) {
                        // TODO: Navegar a gestión de reservas
                        // navController.navigate("")
                    }
                }
            }
        }

    }
}
