package com.example.gesport.repository

import com.example.gesport.models.Team
import kotlinx.coroutines.flow.Flow

interface TeamRepository {

    // ROOM + Compose: Flow para observar cambios
    fun getAllTeams(): Flow<List<Team>>
    fun getTeamsByCategory(category: String): Flow<List<Team>>

    // Equipos de un entrenador
    fun getTeamsByTrainer(trainerId: Int): Flow<List<Team>>

    /** Obtener un equipo por id */
    suspend fun getTeamById(id: Int): Team?

    /** CRUD */
    suspend fun addTeam(team: Team): Team
    suspend fun updateTeam(team: Team): Int
    suspend fun deleteTeam(id: Int): Boolean
}