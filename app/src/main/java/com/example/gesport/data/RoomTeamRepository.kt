package com.example.gesport.data

import com.example.gesport.database.TeamDao
import com.example.gesport.models.Team
import com.example.gesport.repository.TeamRepository
import kotlinx.coroutines.flow.Flow

class RoomTeamRepository(private val teamDao: TeamDao) : TeamRepository {

    override fun getAllTeams(): Flow<List<Team>> =
        teamDao.getAll()

    override fun getTeamsByCategory(category: String): Flow<List<Team>> =
        teamDao.getByCategory(category)


    override fun getTeamsByTrainer(trainerId: Int): Flow<List<Team>> =
        teamDao.getByTrainer(trainerId)


    /** Obtener un equipo por id */
    override suspend fun getTeamById(id: Int): Team? =
        teamDao.getById(id)

    /** Crear equipo nuevo */
    override suspend fun addTeam(team: Team): Team {
        val id = teamDao.insert(team)
        return team.copy(id = id.toInt())
    }

    /** Actualizar equipo existente */
    override suspend fun updateTeam(team: Team): Int {
        val numActualizado = teamDao.update(team)
        return numActualizado
    }

    /** Eliminar equipo por id */
    override suspend fun deleteTeam(id: Int): Boolean {
        val team = teamDao.getById(id)
        return if (team != null) {
            teamDao.delete(team)
            true
        } else {
            false
        }
    }
}