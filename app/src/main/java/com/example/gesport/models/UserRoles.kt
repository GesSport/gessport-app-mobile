package com.example.gesport.models

object UserRoles {
    const val ADMIN_DEPORTIVO = "ADMIN_DEPORTIVO"
    const val ENTRENADOR = "ENTRENADOR"
    const val JUGADOR = "JUGADOR"
    const val ARBITRO = "ARBITRO"

    val allRoles = mapOf(
        ADMIN_DEPORTIVO to "Admin",
        ENTRENADOR to "Entrenador",
        JUGADOR to "Jugador",
        ARBITRO to "Árbitro"
    )

    val registerRoles = listOf(
        ENTRENADOR,
        JUGADOR,
        ARBITRO
    )

    // Colores por rol
    val roleColors = mapOf(
        ADMIN_DEPORTIVO to 0xFF4DA8DAL,
        ENTRENADOR      to 0xFF6ECB63L,
        JUGADOR         to 0xFFF4A261L,
        ARBITRO         to 0xFFE76F51L,
    )
}
