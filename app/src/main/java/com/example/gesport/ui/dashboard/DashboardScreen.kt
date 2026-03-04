package com.example.gesport.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gesport.models.UserRoles
import com.example.gesport.ui.components.DashboardTile
import com.example.gesport.ui.components.GeSportBackgroundScreen

@Composable
fun DashboardScreen(
    navController: NavHostController,
    userId: Int,
    username: String?,
    role: String?
) {
    // Si role viene vacío = JUGADOR
    val roleKey = role?.trim().takeUnless { it.isNullOrEmpty() } ?: UserRoles.JUGADOR
    val isAdmin = roleKey == UserRoles.ADMIN_DEPORTIVO

    // Si no es admin: pantalla de bloqueo (y fuera)
    if (!isAdmin) {
        GeSportBackgroundScreen {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Acceso restringido",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Solo un administrador puede entrar al Dashboard.",
                    color = Color.White.copy(alpha = 0.75f)
                )
                Spacer(Modifier.height(16.dp))
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Volver", color = Color.White)
                }
            }
        }
        return
    }

    val roleLabel = UserRoles.allRoles[roleKey] ?: "Admin"
    val roleColor = Color(UserRoles.roleColors[roleKey] ?: 0xFF2DAAE1L).copy(alpha = 0.70f)

    GeSportBackgroundScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.Start
        ) {

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

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hola, ${username ?: "Administrador"} 👋",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(roleColor)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = roleLabel.uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
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
                    ) { navController.navigate("gesuser") }

                    DashboardTile(
                        label = "Equipos",
                        icon = Icons.Default.Groups,
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate("gesteam") }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardTile(
                        label = "Pistas",
                        icon = Icons.Default.AutoAwesomeMosaic,
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate("gesfacility") }

                    DashboardTile(
                        label = "Reservas",
                        icon = Icons.Default.CalendarToday,
                        modifier = Modifier.weight(1f)
                    ) {
                        navController.navigate("gesreservation/$userId/$roleKey")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardTile(
                        label = "Cerrar sesión",
                        icon = Icons.Default.PowerSettingsNew,
                        modifier = Modifier.weight(1f)
                    ) {
                        navController.navigate("welcome") {
                            popUpTo(0)
                            launchSingleTop = true
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}