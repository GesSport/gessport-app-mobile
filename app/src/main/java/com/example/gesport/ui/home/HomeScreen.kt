package com.example.gesport.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Room
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gesport.models.UserRoles
import com.example.gesport.ui.components.DashboardTile
import com.example.gesport.ui.components.GeSportBackgroundScreen

@Composable
fun HomeScreen(
    navController: NavController,
    userId: Int,
    name: String?,
    role: String?
) {
    val roleKey = remember(role) {
        role?.trim().takeUnless { it.isNullOrEmpty() } ?: UserRoles.JUGADOR
    }
    val roleLabel = UserRoles.allRoles[roleKey] ?: roleKey
    val roleColor = Color(UserRoles.roleColors[roleKey] ?: 0xFF2DAAE1L).copy(alpha = 0.70f)

    val isTrainer = roleKey == UserRoles.ENTRENADOR

    val context = LocalContext.current

    // Estado del diálogo de incidencia
    var showIncidentDialog by remember { mutableStateOf(false) }
    var incidentText by remember { mutableStateOf("") }
    var incidentSent by remember { mutableStateOf(false) }

    fun openMaps() {
        val uri = Uri.parse("geo:0,0?q=${Uri.encode("centro deportivo")}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    // Diálogo de incidencia
    if (showIncidentDialog) {
        AlertDialog(
            onDismissRequest = {
                showIncidentDialog = false
                incidentText = ""
                incidentSent = false
            },
            title = {
                Text(
                    text = if (isTrainer) "Reportar incidencia" else "No puedo asistir",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isTrainer)
                            "Describe la incidencia (p.ej. entrenamiento cancelado, problema en instalación...)"
                        else
                            "Indica el motivo por el que no puedes asistir al próximo entrenamiento",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = incidentText,
                        onValueChange = { incidentText = it },
                        placeholder = {
                            Text(
                                if (isTrainer) "Describe la incidencia..." else "Motivo de ausencia...",
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF2DAAE1),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            cursorColor = Color(0xFF2DAAE1)
                        ),
                        maxLines = 5
                    )
                    if (incidentSent) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "✓ Incidencia enviada correctamente",
                            color = Color(0xFF22C55E),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (incidentText.isNotBlank()) {
                            // Aquí conectarías con tu repositorio para guardar la incidencia
                            // Por ahora mostramos confirmación visual
                            incidentSent = true
                        }
                    }
                ) {
                    Text("Enviar", color = Color(0xFF2DAAE1), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showIncidentDialog = false
                    incidentText = ""
                    incidentSent = false
                }) {
                    Text("Cerrar", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF1A1A2E),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    GeSportBackgroundScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.Start
        ) {

            // HEADER (igual que DashboardScreen)
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
                        text = when (roleKey) {
                            UserRoles.ENTRENADOR -> "Panel de entrenador"
                            UserRoles.ARBITRO    -> "Panel de árbitro"
                            else                 -> "Panel de jugador"
                        },
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = when (roleKey) {
                            UserRoles.ENTRENADOR -> "Gestiona tus equipos, reservas y comunicaciones."
                            UserRoles.ARBITRO    -> "Consulta tus reservas y partidos asignados."
                            else                 -> "Gestiona tus reservas y actividades deportivas."
                        },
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // BIENVENIDA + BADGE ROL
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hola, ${name ?: "Usuario"} 👋",
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

            // GRID DE TILES
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Fila 1: Mis reservas + Nueva reserva
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardTile(
                        label = if (isTrainer) "Mis reservas" else "Mis reservas",
                        icon = Icons.Default.CalendarToday,
                        modifier = Modifier.weight(1f)
                    ) {
                        navController.navigate("gesreservation/$userId/$roleKey")
                    }

                    DashboardTile(
                        label = "Nueva reserva",
                        icon = Icons.Default.EventAvailable,
                        modifier = Modifier.weight(1f)
                    ) {
                        navController.navigate("formreservation/$userId/$roleKey")
                    }
                }

                // Fila 2: Mapa + Mis equipos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardTile(
                        label = "Mapa del centro",
                        icon = Icons.Default.Room,
                        modifier = Modifier.weight(1f)
                    ) {
                        openMaps()
                    }

                    DashboardTile(
                        label = if (isTrainer) "Mis equipos" else "Mi equipo",
                        icon = Icons.Default.Groups,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Reutiliza GesTeamScreen — filtra por rol internamente
                        navController.navigate("gesteam")
                    }
                }

                // Fila 3: Perfil + Incidencias
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardTile(
                        label = "Mi perfil",
                        icon = Icons.Default.Person,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Navega al formulario de edición del propio usuario
                        navController.navigate("formuser/$userId")
                    }

                    DashboardTile(
                        label = if (isTrainer) "Incidencias" else "No puedo asistir",
                        icon = Icons.Default.ReportProblem,
                        modifier = Modifier.weight(1f)
                    ) {
                        showIncidentDialog = true
                    }
                }

                // Fila 4: Cerrar sesión
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
