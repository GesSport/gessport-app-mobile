package com.example.gesport.database

import androidx.room.*
import com.example.gesport.models.Reservation
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {

    // ===================== CREATE =====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reservation: Reservation): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reservations: List<Reservation>)

    // ===================== READ =====================

    // Todas (ADMIN)
    @Query("SELECT * FROM reservas ORDER BY fecha ASC, horaInicio ASC")
    fun getAll(): Flow<List<Reservation>>

    // Por ID (editar)
    @Query("SELECT * FROM reservas WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Reservation?

    // Reservas personales de un usuario
    @Query(
        """
        SELECT * FROM reservas 
        WHERE usuarioId = :userId
        ORDER BY fecha ASC, horaInicio ASC
        """
    )
    fun getByUser(userId: Int): Flow<List<Reservation>>

    // Reservas de un equipo
    @Query(
        """
        SELECT * FROM reservas 
        WHERE equipoId = :teamId
        ORDER BY fecha ASC, horaInicio ASC
        """
    )
    fun getByTeam(teamId: Int): Flow<List<Reservation>>

    // Reservas de varios equipos
    // @Query(
    //     """
    //     SELECT * FROM reservas
    //     WHERE equipoId IN (:teamIds)
    //     ORDER BY fecha ASC, horaInicio ASC
    //     """
    // )
    // fun getByTeams(teamIds: List<Int>): Flow<List<Reservation>>

    // ENTRENADOR: personales + equipos donde es entrenador (UNO solo)
    @Query(
        """
        SELECT * FROM reservas
        WHERE (usuarioId = :trainerId)
           OR (equipoId IN (SELECT id FROM equipos WHERE entrenadorId = :trainerId))
        ORDER BY fecha ASC, horaInicio ASC
        """
    )
    fun getReservationsForTrainer(trainerId: Int): Flow<List<Reservation>>

    // Por pista
    @Query(
        """
        SELECT * FROM reservas 
        WHERE pistaId = :facilityId 
        ORDER BY fecha ASC, horaInicio ASC
        """
    )
    fun getByFacility(facilityId: Int): Flow<List<Reservation>>

    // Por fecha
    @Query(
        """
        SELECT * FROM reservas 
        WHERE fecha = :date 
        ORDER BY horaInicio ASC
        """
    )
    fun getByDate(date: String): Flow<List<Reservation>>

    // Por fecha + pista (validar conflictos)
    @Query(
        """
        SELECT * FROM reservas 
        WHERE fecha = :date 
        AND pistaId = :facilityId 
        ORDER BY horaInicio ASC
        """
    )
    fun getByDateAndFacility(date: String, facilityId: Int): Flow<List<Reservation>>

    // ===================== (HomeScreen) =====================

    // Próximas personales (usuario)
    @Query(
        """
        SELECT * FROM reservas
        WHERE usuarioId = :userId
          AND fecha >= :today
        ORDER BY fecha ASC, horaInicio ASC
        """
    )
    fun getUpcomingByUser(userId: Int, today: String): Flow<List<Reservation>>

    // Próximas de un equipo
    @Query(
        """
        SELECT * FROM reservas
        WHERE equipoId = :teamId
          AND fecha >= :today
        ORDER BY fecha ASC, horaInicio ASC
        """
    )
    fun getUpcomingByTeam(teamId: Int, today: String): Flow<List<Reservation>>

    // Próximas para entrenador (personales + sus equipos)
    @Query(
        """
        SELECT * FROM reservas
        WHERE (
            (usuarioId = :trainerId)
            OR (equipoId IN (SELECT id FROM equipos WHERE entrenadorId = :trainerId))
        )
        AND fecha >= :today
        ORDER BY fecha ASC, horaInicio ASC
        """
    )
    fun getUpcomingReservationsForTrainer(trainerId: Int, today: String): Flow<List<Reservation>>

    // ===================== UPDATE =====================

    @Update
    suspend fun update(reservation: Reservation): Int

    // ===================== DELETE =====================

    @Delete
    suspend fun delete(reservation: Reservation)

    @Query("DELETE FROM reservas WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM reservas")
    suspend fun deleteAll()
}