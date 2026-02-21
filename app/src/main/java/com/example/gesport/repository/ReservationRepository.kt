package com.example.gesport.repository

import com.example.gesport.models.Reservation
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {

    // ROOM + Compose
    fun getAllReservations(): Flow<List<Reservation>>
    fun getReservationsByDate(date: String): Flow<List<Reservation>>
    fun getReservationsByDateAndFacility(date: String, facilityId: Int): Flow<List<Reservation>>
    fun getReservationsByUser(userId: Int): Flow<List<Reservation>>
    fun getReservationsByFacility(facilityId: Int): Flow<List<Reservation>>

    /** Obtener una reserva por id */
    suspend fun getReservationById(id: Int): Reservation?

    /** CRUD */
    suspend fun addReservation(reservation: Reservation): Reservation
    suspend fun updateReservation(reservation: Reservation): Int
    suspend fun deleteReservation(id: Int): Boolean
}