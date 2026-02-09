package com.example.gesport.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.gesport.models.Reservation

@Dao
interface ReservationDao {

    // ==================== INSERTAR ====================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reservation: Reservation): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reservations: List<Reservation>)

    // ==================== ACTUALIZAR ====================
    @Update
    suspend fun update(reservation: Reservation)

    // ==================== ELIMINAR ====================
    @Delete
    suspend fun delete(reservation: Reservation)

    @Query("DELETE FROM reservas WHERE id = :reservationId")
    suspend fun deleteById(reservationId: Int)

    @Query("DELETE FROM reservas")
    suspend fun deleteAll()

    // ==================== OBTENER TODAS ====================
    @Query("SELECT * FROM reservas ORDER BY fecha DESC, horaInicio ASC")
    fun getAllReservations(): LiveData<List<Reservation>>

    @Query("SELECT * FROM reservas ORDER BY fecha DESC, horaInicio ASC")
    suspend fun getAllReservationsSync(): List<Reservation>

    // ==================== OBTENER POR ID ====================
    @Query("SELECT * FROM reservas WHERE id = :reservationId")
    suspend fun getReservationById(reservationId: Int): Reservation?

    @Query("SELECT * FROM reservas WHERE id = :reservationId")
    fun getReservationByIdLiveData(reservationId: Int): LiveData<Reservation?>

    // ==================== RESERVAS DE UNA PISTA ====================
    @Query("SELECT * FROM reservas WHERE pistaId = :pistaId ORDER BY fecha DESC, horaInicio ASC")
    fun getReservationsByFacility(pistaId: Int): LiveData<List<Reservation>>

    @Query("SELECT * FROM reservas WHERE pistaId = :pistaId ORDER BY fecha DESC, horaInicio ASC")
    suspend fun getReservationsByFacilitySync(pistaId: Int): List<Reservation>

    @Query("SELECT * FROM reservas WHERE pistaId = :pistaId AND fecha = :fecha ORDER BY horaInicio ASC")
    fun getReservationsByFacilityAndDate(pistaId: Int, fecha: String): LiveData<List<Reservation>>

    @Query("SELECT * FROM reservas WHERE pistaId = :pistaId AND fecha = :fecha ORDER BY horaInicio ASC")
    suspend fun getReservationsByFacilityAndDateSync(pistaId: Int, fecha: String): List<Reservation>

    // ==================== RESERVAS DE UN USUARIO ====================
    @Query("SELECT * FROM reservas WHERE usuarioId = :usuarioId ORDER BY fecha DESC, horaInicio ASC")
    fun getReservationsByUser(usuarioId: Int): LiveData<List<Reservation>>

    @Query("SELECT * FROM reservas WHERE usuarioId = :usuarioId ORDER BY fecha DESC, horaInicio ASC")
    suspend fun getReservationsByUserSync(usuarioId: Int): List<Reservation>

    // ==================== RESERVAS POR FECHA ====================
    @Query("SELECT * FROM reservas WHERE fecha = :fecha ORDER BY horaInicio ASC")
    fun getReservationsByDate(fecha: String): LiveData<List<Reservation>>

    @Query("SELECT * FROM reservas WHERE fecha = :fecha ORDER BY horaInicio ASC")
    suspend fun getReservationsByDateSync(fecha: String): List<Reservation>

    @Query("SELECT * FROM reservas WHERE fecha >= :fechaInicio AND fecha <= :fechaFin ORDER BY fecha ASC, horaInicio ASC")
    fun getReservationsByDateRange(fechaInicio: String, fechaFin: String): LiveData<List<Reservation>>

    @Query("SELECT * FROM reservas WHERE fecha >= :fechaInicio AND fecha <= :fechaFin ORDER BY fecha ASC, horaInicio ASC")
    suspend fun getReservationsByDateRangeSync(fechaInicio: String, fechaFin: String): List<Reservation>

    // ==================== PRÓXIMAS RESERVAS ====================
    @Query("SELECT * FROM reservas WHERE fecha >= :fechaActual ORDER BY fecha ASC, horaInicio ASC LIMIT :limit")
    suspend fun getUpcomingReservations(fechaActual: String, limit: Int = 10): List<Reservation>

    @Query("SELECT * FROM reservas WHERE usuarioId = :usuarioId AND fecha >= :fechaActual ORDER BY fecha ASC, horaInicio ASC LIMIT :limit")
    suspend fun getUpcomingReservationsByUser(usuarioId: Int, fechaActual: String, limit: Int = 10): List<Reservation>

    // ==================== RESERVAS PASADAS ====================
    @Query("SELECT * FROM reservas WHERE fecha < :fechaActual ORDER BY fecha DESC, horaInicio DESC LIMIT :limit")
    suspend fun getPastReservations(fechaActual: String, limit: Int = 10): List<Reservation>

    @Query("SELECT * FROM reservas WHERE usuarioId = :usuarioId AND fecha < :fechaActual ORDER BY fecha DESC, horaInicio DESC LIMIT :limit")
    suspend fun getPastReservationsByUser(usuarioId: Int, fechaActual: String, limit: Int = 10): List<Reservation>

    // ==================== RESERVAS POR TIPO DE USO ====================
    @Query("SELECT * FROM reservas WHERE tipoUso = :tipoUso ORDER BY fecha DESC, horaInicio ASC")
    fun getReservationsByType(tipoUso: String): LiveData<List<Reservation>>

    @Query("SELECT * FROM reservas WHERE tipoUso = :tipoUso ORDER BY fecha DESC, horaInicio ASC")
    suspend fun getReservationsByTypeSync(tipoUso: String): List<Reservation>

    // ==================== VERIFICAR SOLAPAMIENTO ====================
    @Query("""
        SELECT COUNT(*) FROM reservas 
        WHERE pistaId = :pistaId 
        AND fecha = :fecha 
        AND (
            (horaInicio < :horaFin AND horaFin > :horaInicio)
        )
    """)
    suspend fun checkOverlap(
        pistaId: Int,
        fecha: String,
        horaInicio: String,
        horaFin: String
    ): Int

    @Query("""
        SELECT COUNT(*) FROM reservas 
        WHERE pistaId = :pistaId 
        AND fecha = :fecha 
        AND (
            (horaInicio < :horaFin AND horaFin > :horaInicio)
        )
        AND id != :excludeId
    """)
    suspend fun checkOverlapExcluding(
        pistaId: Int,
        fecha: String,
        horaInicio: String,
        horaFin: String,
        excludeId: Int
    ): Int

    // ==================== OBTENER RESERVAS CON CONFLICTO ====================
    @Query("""
        SELECT * FROM reservas 
        WHERE pistaId = :pistaId 
        AND fecha = :fecha 
        AND (
            (horaInicio < :horaFin AND horaFin > :horaInicio)
        )
        ORDER BY horaInicio ASC
    """)
    suspend fun getOverlappingReservations(
        pistaId: Int,
        fecha: String,
        horaInicio: String,
        horaFin: String
    ): List<Reservation>

    // ==================== VERIFICAR DISPONIBILIDAD ====================
    @Query("""
        SELECT COUNT(*) = 0 FROM reservas 
        WHERE pistaId = :pistaId 
        AND fecha = :fecha 
        AND (
            (horaInicio < :horaFin AND horaFin > :horaInicio)
        )
    """)
    suspend fun isTimeSlotAvailable(
        pistaId: Int,
        fecha: String,
        horaInicio: String,
        horaFin: String
    ): Boolean

    // ==================== ELIMINAR RESERVAS ANTIGUAS ====================
    @Query("DELETE FROM reservas WHERE fecha < :fechaLimite")
    suspend fun deletePastReservations(fechaLimite: String): Int

    @Query("DELETE FROM reservas WHERE usuarioId = :usuarioId")
    suspend fun deleteReservationsByUser(usuarioId: Int)

    @Query("DELETE FROM reservas WHERE pistaId = :pistaId")
    suspend fun deleteReservationsByFacility(pistaId: Int)

    // ==================== CONTADORES ====================
    @Query("SELECT COUNT(*) FROM reservas")
    suspend fun getReservationCount(): Int

    @Query("SELECT COUNT(*) FROM reservas WHERE fecha = :fecha")
    suspend fun getReservationCountByDate(fecha: String): Int

    @Query("SELECT COUNT(*) FROM reservas WHERE usuarioId = :usuarioId")
    suspend fun getReservationCountByUser(usuarioId: Int): Int

    @Query("SELECT COUNT(*) FROM reservas WHERE pistaId = :pistaId")
    suspend fun getReservationCountByFacility(pistaId: Int): Int

    // ==================== BÚSQUEDA ====================
    @Query("SELECT * FROM reservas WHERE tipoUso LIKE '%' || :query || '%' ORDER BY fecha DESC, horaInicio ASC")
    fun searchReservations(query: String): LiveData<List<Reservation>>

    @Query("SELECT * FROM reservas WHERE tipoUso LIKE '%' || :query || '%' ORDER BY fecha DESC, horaInicio ASC")
    suspend fun searchReservationsSync(query: String): List<Reservation>

    // ==================== ESTADÍSTICAS ====================
    @Query("""
        SELECT tipoUso, COUNT(*) as count 
        FROM reservas 
        WHERE tipoUso IS NOT NULL
        GROUP BY tipoUso 
        ORDER BY count DESC
    """)
    suspend fun getReservationCountByTypes(): Map<String, Int>

    @Query("""
        SELECT fecha, COUNT(*) as count 
        FROM reservas 
        GROUP BY fecha 
        ORDER BY fecha DESC 
        LIMIT :limit
    """)
    suspend fun getReservationCountByDates(limit: Int = 30): Map<String, Int>
}