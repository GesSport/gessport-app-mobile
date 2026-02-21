package com.example.gesport.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "usuarios")
@Serializable
data class User (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String,
    val fechaNacimiento: String? = null,
    val telefono: String? = null,
    val posicion: String? = null,
    val equipoId: Int? = null
)