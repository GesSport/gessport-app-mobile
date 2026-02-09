package com.example.gesport.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "reservas",
)
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pistaId: Int,
    val usuarioId: Int,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val tipoUso: String? = null
)