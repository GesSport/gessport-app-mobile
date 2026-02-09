package com.example.gesport.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "partidos",
)
data class Match(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fecha: String,
    val hora: String,
    val equipoLocalId: Int,
    val equipoVisitanteId: Int,
    val arbitroId: Int? = null,
    val liga: String? = null,
    val resultadoLocal: Int? = null,
    val resultadoVisitante: Int? = null
)