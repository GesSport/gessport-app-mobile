package com.example.gesport.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservas")
data class Reservation(
    @PrimaryKey(autoGenerate = true)

    val id: Int = 0,
    val pistaId: Int,

    /**
     * Reserva personal -> usuarioId != null && equipoId == null
     * Reserva de equipo -> equipoId != null && usuarioId == null
     */
    val usuarioId: Int? = null,
    val equipoId: Int? = null,

    /**
     * Quién crea la reserva (sirve para auditoría y permisos)
     * - Si es reserva de equipo: normalmente será el ENTRENADOR o ADMIN
     * - Si es personal: será el propio usuario (o admin si la crea desde panel)
     */
    val creadaPorUserId: Int,

    val fecha: String,       // yyyy-MM-dd
    val horaInicio: String,  // HH:mm
    val horaFin: String,     // HH:mm
    val tipoUso: String? = null
)