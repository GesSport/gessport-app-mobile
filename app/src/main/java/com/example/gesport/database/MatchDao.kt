package com.example.gesport.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.gesport.models.Match

@Dao
interface MatchDao {

    // ==================== INSERTAR ====================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(match: Match): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(matches: List<Match>)

    // ==================== ACTUALIZAR ====================
    @Update
    suspend fun update(match: Match)

    // ==================== ELIMINAR ====================
    @Delete
    suspend fun delete(match: Match)

    @Query("DELETE FROM partidos WHERE id = :matchId")
    suspend fun deleteById(matchId: Int)

    @Query("DELETE FROM partidos")
    suspend fun deleteAll()

    // ==================== OBTENER TODOS ====================
    @Query("SELECT * FROM partidos ORDER BY fecha DESC, hora DESC")
    fun getAllMatches(): LiveData<List<Match>>

    @Query("SELECT * FROM partidos ORDER BY fecha DESC, hora DESC")
    suspend fun getAllMatchesSync(): List<Match>

    // ==================== OBTENER POR ID ====================
    @Query("SELECT * FROM partidos WHERE id = :matchId")
    suspend fun getMatchById(matchId: Int): Match?

    @Query("SELECT * FROM partidos WHERE id = :matchId")
    fun getMatchByIdLiveData(matchId: Int): LiveData<Match?>

    // ==================== PARTIDOS DE UN EQUIPO ====================
    @Query("SELECT * FROM partidos WHERE equipoLocalId = :teamId OR equipoVisitanteId = :teamId ORDER BY fecha DESC, hora DESC")
    fun getMatchesByTeam(teamId: Int): LiveData<List<Match>>

    @Query("SELECT * FROM partidos WHERE equipoLocalId = :teamId OR equipoVisitanteId = :teamId ORDER BY fecha DESC, hora DESC")
    suspend fun getMatchesByTeamSync(teamId: Int): List<Match>

    // ==================== PARTIDOS COMO LOCAL ====================
    @Query("SELECT * FROM partidos WHERE equipoLocalId = :teamId ORDER BY fecha DESC, hora DESC")
    fun getHomeMatchesByTeam(teamId: Int): LiveData<List<Match>>

    @Query("SELECT * FROM partidos WHERE equipoLocalId = :teamId ORDER BY fecha DESC, hora DESC")
    suspend fun getHomeMatchesByTeamSync(teamId: Int): List<Match>

    // ==================== PARTIDOS COMO VISITANTE ====================
    @Query("SELECT * FROM partidos WHERE equipoVisitanteId = :teamId ORDER BY fecha DESC, hora DESC")
    fun getAwayMatchesByTeam(teamId: Int): LiveData<List<Match>>

    @Query("SELECT * FROM partidos WHERE equipoVisitanteId = :teamId ORDER BY fecha DESC, hora DESC")
    suspend fun getAwayMatchesByTeamSync(teamId: Int): List<Match>

    // ==================== PARTIDOS DE UN ÁRBITRO ====================
    @Query("SELECT * FROM partidos WHERE arbitroId = :arbitroId ORDER BY fecha DESC, hora DESC")
    fun getMatchesByReferee(arbitroId: Int): LiveData<List<Match>>

    @Query("SELECT * FROM partidos WHERE arbitroId = :arbitroId ORDER BY fecha DESC, hora DESC")
    suspend fun getMatchesByRefereeSync(arbitroId: Int): List<Match>

    // ==================== PARTIDOS SIN ÁRBITRO ====================
    @Query("SELECT * FROM partidos WHERE arbitroId IS NULL ORDER BY fecha ASC, hora ASC")
    fun getMatchesWithoutReferee(): LiveData<List<Match>>

    @Query("SELECT * FROM partidos WHERE arbitroId IS NULL ORDER BY fecha ASC, hora ASC")
    suspend fun getMatchesWithoutRefereeSync(): List<Match>

    // ==================== PARTIDOS POR FECHA ====================
    @Query("SELECT * FROM partidos WHERE fecha = :fecha ORDER BY hora ASC")
    fun getMatchesByDate(fecha: String): LiveData<List<Match>>

    @Query("SELECT * FROM partidos WHERE fecha = :fecha ORDER BY hora ASC")
    suspend fun getMatchesByDateSync(fecha: String): List<Match>

    @Query("SELECT * FROM partidos WHERE fecha >= :fechaInicio AND fecha <= :fechaFin ORDER BY fecha ASC, hora ASC")
    fun getMatchesByDateRange(fechaInicio: String, fechaFin: String): LiveData<List<Match>>

    @Query("SELECT * FROM partidos WHERE fecha >= :fechaInicio AND fecha <= :fechaFin ORDER BY fecha ASC, hora ASC")
    suspend fun getMatchesByDateRangeSync(fechaInicio: String, fechaFin: String): List<Match>

    // ==================== PARTIDOS POR LIGA ====================
    @Query("SELECT * FROM partidos WHERE liga = :liga ORDER BY fecha DESC, hora DESC")
    fun getMatchesByLeague(liga: String): LiveData<List<Match>>

    @Query("SELECT * FROM partidos WHERE liga = :liga ORDER BY fecha DESC, hora DESC")
    suspend fun getMatchesByLeagueSync(liga: String): List<Match>

    // ==================== PARTIDOS FINALIZADOS ====================
    @Query("SELECT * FROM partidos WHERE resultadoLocal IS NOT NULL AND resultadoVisitante IS NOT NULL ORDER BY fecha DESC, hora DESC")
    fun getFinishedMatches(): LiveData<List<Match>>

    @Query("SELECT * FROM partidos WHERE resultadoLocal IS NOT NULL AND resultadoVisitante IS NOT NULL ORDER BY fecha DESC, hora DESC")
    suspend fun getFinishedMatchesSync(): List<Match>

    // ==================== PARTIDOS PENDIENTES ====================
    @Query("SELECT * FROM partidos WHERE resultadoLocal IS NULL OR resultadoVisitante IS NULL ORDER BY fecha ASC, hora ASC")
    fun getPendingMatches(): LiveData<List<Match>>

    @Query("SELECT * FROM partidos WHERE resultadoLocal IS NULL OR resultadoVisitante IS NULL ORDER BY fecha ASC, hora ASC")
    suspend fun getPendingMatchesSync(): List<Match>

    // ==================== PRÓXIMOS PARTIDOS ====================
    @Query("SELECT * FROM partidos WHERE fecha >= :fechaActual AND (resultadoLocal IS NULL OR resultadoVisitante IS NULL) ORDER BY fecha ASC, hora ASC LIMIT :limit")
    suspend fun getUpcomingMatches(fechaActual: String, limit: Int = 10): List<Match>

    // ==================== VERIFICACIONES ====================
    @Query("""
        SELECT COUNT(*) FROM partidos 
        WHERE fecha = :fecha 
        AND ((equipoLocalId = :equipo1 AND equipoVisitanteId = :equipo2) 
             OR (equipoLocalId = :equipo2 AND equipoVisitanteId = :equipo1))
    """)
    suspend fun matchExists(fecha: String, equipo1: Int, equipo2: Int): Int

    @Query("""
        SELECT COUNT(*) FROM partidos 
        WHERE fecha = :fecha 
        AND ((equipoLocalId = :equipo1 AND equipoVisitanteId = :equipo2) 
             OR (equipoLocalId = :equipo2 AND equipoVisitanteId = :equipo1))
        AND id != :excludeId
    """)
    suspend fun matchExistsExcluding(fecha: String, equipo1: Int, equipo2: Int, excludeId: Int): Int

    @Query("SELECT COUNT(*) FROM partidos WHERE equipoLocalId = :teamId AND equipoVisitanteId = :teamId")
    suspend fun hasSelfMatch(teamId: Int): Int

    @Query("SELECT COUNT(*) FROM partidos")
    suspend fun getMatchCount(): Int

    // ==================== ACTUALIZAR RESULTADO ====================
    @Query("UPDATE partidos SET resultadoLocal = :resultadoLocal, resultadoVisitante = :resultadoVisitante WHERE id = :matchId")
    suspend fun updateResult(matchId: Int, resultadoLocal: Int, resultadoVisitante: Int)

    @Query("UPDATE partidos SET arbitroId = :arbitroId WHERE id = :matchId")
    suspend fun assignReferee(matchId: Int, arbitroId: Int)

    // ==================== BÚSQUEDA ====================
    @Query("SELECT * FROM partidos WHERE liga LIKE '%' || :query || '%' ORDER BY fecha DESC, hora DESC")
    fun searchMatches(query: String): LiveData<List<Match>>

    @Query("SELECT * FROM partidos WHERE liga LIKE '%' || :query || '%' ORDER BY fecha DESC, hora DESC")
    suspend fun searchMatchesSync(query: String): List<Match>
}
