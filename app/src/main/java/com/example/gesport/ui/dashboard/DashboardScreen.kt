package com.example.gesport.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gesport.R

@Composable
fun DashboardScreen(
    navController: NavHostController,
    username: String?
) {
    Box(Modifier.fillMaxSize()) {

        // Fondo
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Capa oscura
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.70f),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.Start
            ) {

                // HEADER: título + subtítulo (solo esto)
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

                // Bajamos el saludo
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Hola, ${username ?: "Administrador"} 👋",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                // GRID DE BOTONES (2x2)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AdminTile(
                            label = "Usuarios",
                            icon = Icons.Default.Person,
                            modifier = Modifier.weight(1f)
                        ) {
                            // 👉 Navega al panel de usuarios que ya tienes
                            navController.navigate("gesuser")
                        }

                        AdminTile(
                            label = "Equipos",
                            icon = Icons.Default.Build,
                            modifier = Modifier.weight(1f)
                        ) {
                            // TODO: Navegar a gestión de equipos
                            // navController.navigate("teams")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AdminTile(
                            label = "Pistas",
                            icon = Icons.Default.Build,
                            modifier = Modifier.weight(1f)
                        ) {
                            // TODO: Navegar a gestión de pistas
                            // navController.navigate("courts")
                        }

                        AdminTile(
                            label = "Reservas",
                            icon = Icons.Default.Build,
                            modifier = Modifier.weight(1f)
                        ) {
                            // TODO: Navegar a gestión de reservas
                            // navController.navigate("bookings")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminTile(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .aspectRatio(1f) // cuadrado
            .clickable { onClick() },
        shape = RoundedCornerShape(4.dp),
        color = Color(0xFF2DAAE1).copy(alpha = 0.40f) // mismo azul que el botón, con ligera transparencia
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = label,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}
