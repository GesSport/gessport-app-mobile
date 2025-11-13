package com.example.gesport.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gesport.R
import com.example.gesport.domain.LoginLogic
import com.example.gesport.ui.login.components.GoogleButton
import com.example.gesport.ui.login.components.Input
import com.example.gesport.ui.login.components.PasswordInput
import com.example.gesport.ui.login.components.PrimaryButton

@Composable
fun LoginScreen(navController: NavHostController) {
    val logic = remember { LoginLogic() }

    // MVVM: view model (es una clase aparte) donde tendremos las variables que usaremos en otras pantallas para mantener la info entre ellas. (observable) →
    var email by remember { mutableStateOf("")}
    var password by rememberSaveable { mutableStateOf("") }
    var rememberMe by rememberSaveable { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize()) {
        // Fondo
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center),
            color = Color.Black.copy(alpha = 0.75f),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título + Logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier.size(65.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Bienvenido a\nGeSport",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        textAlign = TextAlign.Start
                    )
                }

                // Subtítulo
                Text(
                    "Entrena. Conecta. Mejora.",
                    color = Color.White.copy(alpha = 0.35f),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(0.5.dp))

                // Input Correo electrónico
                Input(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Correo electrónico",
                    leadingIconRes = R.drawable.icon_email
                )


                // Input Contraseña
                PasswordInput(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Contraseña"
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Checkbox "Recuérdame" + botón "¿Olvidaste…? Pulsa aquí"
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF2DAAE1),
                                uncheckedColor = Color.White.copy(alpha = 0.65f),
                                checkmarkColor = Color.White,
                                disabledUncheckedColor = Color.Transparent
                            )
                        )
                        Text("Recuérdame", color = Color.White.copy(alpha = 0.65f))
                    }

                    Row(
                        modifier = Modifier.padding(top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "¿Olvidaste tu contraseña?",
                            color = Color.White.copy(alpha = 0.65f)
                        )
                        TextButton(
                            onClick = { navController.navigate("recover") },
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFF2DAAE1).copy(alpha = 0.75f)
                            )
                        ) {
                            Text("Pulsa aquí")
                        }
                    }
                }

                // Botón "Iniciar sesión"
                PrimaryButton(
                    text = "Iniciar sesión",
                    onClick = {
                        try {
                            val user = logic.checkLogin(email, password)
                            navController.navigate("home/${user.name}")
                        } catch (e: IllegalArgumentException) {
                            errorMessage = e.message.toString()
                        }
                    }
                )

                // Botón "Iniciar sesión con Google"
                GoogleButton(
                    onClick = {
                        // TODO: Implementar inicio de sesión con Google
                    }
                )

                // Botón “¿No tienes cuenta? Crea una aquí”
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("¿No tienes cuenta?", color = Color.White.copy(alpha = 0.65f))
                    TextButton(
                        onClick = { navController.navigate("register") },
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF2DAAE1).copy(alpha = 0.75f)
                        )
                    ) {
                        Text("Crea una aquí")
                    }
                }
            }
        }
    }
}
