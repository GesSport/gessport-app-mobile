package com.example.gesport.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gesport.R
import com.example.gesport.ui.components.GeSportBackgroundScreen
import com.example.gesport.ui.components.Input
import com.example.gesport.ui.components.PrimaryButton

/**
 * Pantalla de recuperación de contraseña.
 *
 * Permite al usuario introducir su correo electrónico para solicitar
 * instrucciones de restablecimiento de contraseña.
 *
 * TODO: Lógica de envío. De momento, se muestra solo un mensaje informativo.
 *
 */
@Composable
fun RecoverPassScreen(navController: NavController) {
    // Campo de entrada del email
    var email by rememberSaveable { mutableStateOf("") }

    // Mensaje informativo al pulsar botón "Enviar"
    var infoMessage by rememberSaveable { mutableStateOf("") }

    // Componente reutilizable que aplica el fondo y la capa oscura
    GeSportBackgroundScreen {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Contenedor Título + Texto explicativo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(225.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.BottomStart),
                    horizontalAlignment = Alignment.Start
                ) {

                    Spacer(Modifier.height(20.dp))

                    // Título principal
                    Text(
                        text = "Recuperar contraseña",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Texto explicativo
            Text(
                text = "Introduce tu correo electrónico y, si se encuentra registrado en la base de datos, recibirás un email con las instrucciones para restablecer tu contraseña.",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(6.dp))

            // Input Correo electrónico
            Input(
                value = email,
                onValueChange = { email = it },
                placeholder = "Correo electrónico",
                leadingIconRes = R.drawable.icon_email
            )

            Spacer(Modifier.width(1.dp))

            // Botón "Enviar"
            PrimaryButton(
                text = "Enviar",
                onClick = {
                    // TODO: Implementar la lógica
                    infoMessage = "Si el correo está registrado, recibirás un email con las instrucciones. No olvides revisar el apartado de spam :)"
                }
            )

            // Mensaje informativo
            if (infoMessage.isNotEmpty()) {
                Text(
                    text = infoMessage,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Botón “¿No tienes cuenta? Crea una aquí”
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿Ya la recuerdas?", color = Color.White.copy(alpha = 0.65f))
                TextButton(
                    onClick = { navController.navigate("login") },
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF2DAAE1).copy(alpha = 0.75f)
                    )
                ) {
                    Text("Inicia sesión aquí")
                }
            }

        }
    }
}
