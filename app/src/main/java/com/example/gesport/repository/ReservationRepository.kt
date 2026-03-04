package com.example.gesport.repository

import com.example.gesport.models.Reservation
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {

    // ADMIN
    fun getAllReservations(): Flow<List<Reservation>>

    // FORM
    suspend fun getReservationById(id: Int): Reservation?
    suspend fun addReservation(reservation: Reservation): Reservation
    suspend fun updateReservation(reservation: Reservation): Int
    suspend fun deleteReservation(id: Int): Boolean

    // GRID (fecha + pista)
    fun getReservationsByDateAndFacility(date: String, facilityId: Int): Flow<List<Reservation>>

    // Por rol
    fun getReservationsByUser(userId: Int): Flow<List<Reservation>>
    fun getReservationsByTeam(teamId: Int): Flow<List<Reservation>>

    // ENTRENADOR: personales + equipos donde es entrenador
    fun getReservationsForTrainer(trainerId: Int): Flow<List<Reservation>>
}