package com.example.gesport.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gesport.models.Reservation
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {

    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reservation: Reservation): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reservations: List<Reservation>)

    // READ (listado principal)
    @Query("SELECT * FROM reservas ORDER BY fecha ASC, horaInicio ASC")
    fun getAll(): Flow<List<Reservation>>

    // READ (para editar)
    @Query("SELECT * FROM reservas WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Reservation?

    // READ (por usuario)
    @Query("SELECT * FROM reservas WHERE usuarioId = :userId ORDER BY fecha ASC, horaInicio ASC")
    fun getByUser(userId: Int): Flow<List<Reservation>>

    // READ (por pista/instalación)
    @Query("SELECT * FROM reservas WHERE pistaId = :facilityId ORDER BY fecha ASC, horaInicio ASC")
    fun getByFacility(facilityId: Int): Flow<List<Reservation>>

    // READ (por fecha)
    @Query("SELECT * FROM reservas WHERE fecha = :date ORDER BY horaInicio ASC")
    fun getByDate(date: String): Flow<List<Reservation>>

    // READ (por fecha + pista) -> útil para "reservas por franja horaria"
    @Query("SELECT * FROM reservas WHERE fecha = :date AND pistaId = :facilityId ORDER BY horaInicio ASC")
    fun getByDateAndFacility(date: String, facilityId: Int): Flow<List<Reservation>>

    // UPDATE
    @Update
    suspend fun update(reservation: Reservation): Int

    // DELETE
    @Delete
    suspend fun delete(reservation: Reservation)

    @Query("DELETE FROM reservas WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM reservas")
    suspend fun deleteAll()
}