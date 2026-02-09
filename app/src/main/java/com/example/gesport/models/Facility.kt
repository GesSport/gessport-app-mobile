package com.example.gesport.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "instalaciones")
data class Facility(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val tipoDeporte: String,
    val disponible: Boolean = true,
    val capacidad: Int? = null
)