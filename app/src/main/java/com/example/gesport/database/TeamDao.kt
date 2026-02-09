package com.example.gesport.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.gesport.models.Team

@Dao
interface TeamDao {

    // ==================== INSERTAR ====================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(team: Team): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(teams: List<Team>)

    // ==================== ACTUALIZAR ====================
    @Update
    suspend fun update(team: Team)

    // ==================== ELIMINAR ====================
    @Delete
    suspend fun delete(team: Team)

    @Query("DELETE FROM equipos WHERE id = :teamId")
    suspend fun deleteById(teamId: Int)

    @Query("DELETE FROM equipos")
    suspend fun deleteAll()

    // ==================== OBTENER TODOS ====================
    @Query("SELECT * FROM equipos ORDER BY nombre ASC")
    fun getAllTeams(): LiveData<List<Team>>

    @Query("SELECT * FROM equipos ORDER BY nombre ASC")
    suspend fun getAllTeamsSync(): List<Team>

    // ==================== OBTENER POR ID ====================
    @Query("SELECT * FROM equipos WHERE id = :teamId")
    suspend fun getTeamById(teamId: Int): Team?

    @Query("SELECT * FROM equipos WHERE id = :teamId")
    fun getTeamByIdLiveData(teamId: Int): LiveData<Team?>

    // ==================== OBTENER POR CATEGORÍA ====================
    @Query("SELECT * FROM equipos WHERE categoria = :categoria ORDER BY nombre ASC")
    fun getTeamsByCategory(categoria: String): LiveData<List<Team>>

    @Query("SELECT * FROM equipos WHERE categoria = :categoria ORDER BY nombre ASC")
    suspend fun getTeamsByCategorySync(categoria: String): List<Team>

    // ==================== EQUIPOS DE UN ENTRENADOR ====================
    @Query("SELECT * FROM equipos WHERE entrenadorId = :entrenadorId ORDER BY nombre ASC")
    fun getTeamsByCoach(entrenadorId: Int): LiveData<List<Team>>

    @Query("SELECT * FROM equipos WHERE entrenadorId = :entrenadorId ORDER BY nombre ASC")
    suspend fun getTeamsByCoachSync(entrenadorId: Int): List<Team>

    // ==================== EQUIPOS SIN ENTRENADOR ====================
    @Query("SELECT * FROM equipos WHERE entrenadorId IS NULL ORDER BY nombre ASC")
    fun getTeamsWithoutCoach(): LiveData<List<Team>>

    @Query("SELECT * FROM equipos WHERE entrenadorId IS NULL ORDER BY nombre ASC")
    suspend fun getTeamsWithoutCoachSync(): List<Team>

    // ==================== VERIFICACIONES ====================
    @Query("SELECT COUNT(*) FROM equipos WHERE nombre = :nombre")
    suspend fun teamNameExists(nombre: String): Int

    @Query("SELECT COUNT(*) FROM equipos WHERE nombre = :nombre AND id != :excludeId")
    suspend fun teamNameExistsExcluding(nombre: String, excludeId: Int): Int

    @Query("SELECT COUNT(*) FROM equipos")
    suspend fun getTeamCount(): Int

    @Query("SELECT COUNT(*) FROM equipos WHERE categoria = :categoria")
    suspend fun getTeamCountByCategory(categoria: String): Int

    // ==================== BÚSQUEDA ====================
    @Query("SELECT * FROM equipos WHERE nombre LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun searchTeams(query: String): LiveData<List<Team>>

    @Query("SELECT * FROM equipos WHERE nombre LIKE '%' || :query || '%' ORDER BY nombre ASC")
    suspend fun searchTeamsSync(query: String): List<Team>

    // ==================== ESTADÍSTICAS ====================
    @Query("""
        SELECT categoria, COUNT(*) as count 
        FROM equipos 
        GROUP BY categoria 
        ORDER BY count DESC
    """)
    suspend fun getTeamCountByCategories(): Map<String, Int>
}
