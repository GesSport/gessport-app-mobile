package com.example.gesport.models

object Sports {

    val allSports = listOf(
        "FUTBOL" to "Fútbol",
        "PADEL" to "Pádel",
        "ATLETISMO" to "Atletismo",
        "BALONCESTO" to "Baloncesto",
        "NATACION" to "Natación",
        "TENIS" to "Tenis"
    )

    fun labelFor(key: String): String {
        val normalized = key.trim().uppercase()
        return allSports.firstOrNull { it.first == normalized }?.second ?: key.trim()
    }
}