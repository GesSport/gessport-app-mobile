package com.example.gesport.data

import com.example.gesport.database.ReservationDao
import com.example.gesport.models.Reservation
import com.example.gesport.repository.ReservationRepository
import kotlinx.coroutines.flow.Flow

class RoomReservationRepository(private val reservationDao: ReservationDao) : ReservationRepository {

    override fun getAllReservations(): Flow<List<Reservation>> =
        reservationDao.getAll()

    override fun getReservationsByDate(date: String): Flow<List<Reservation>> =
        reservationDao.getByDate(date)

    override fun getReservationsByDateAndFacility(date: String, facilityId: Int): Flow<List<Reservation>> =
        reservationDao.getByDateAndFacility(date, facilityId)

    override fun getReservationsByUser(userId: Int): Flow<List<Reservation>> =
        reservationDao.getByUser(userId)

    override fun getReservationsByFacility(facilityId: Int): Flow<List<Reservation>> =
        reservationDao.getByFacility(facilityId)

    /** Obtener una reserva por id */
    override suspend fun getReservationById(id: Int): Reservation? =
        reservationDao.getById(id)

    /** Crear reserva nueva */
    override suspend fun addReservation(reservation: Reservation): Reservation {
        val id = reservationDao.insert(reservation)
        return reservation.copy(id = id.toInt())
    }

    /** Actualizar reserva existente */
    override suspend fun updateReservation(reservation: Reservation): Int {
        val numActualizado = reservationDao.update(reservation)
        return numActualizado
    }

    /** Eliminar reserva por id */
    override suspend fun deleteReservation(id: Int): Boolean {
        val reservation = reservationDao.getById(id)
        return if (reservation != null) {
            reservationDao.delete(reservation)
            true
        } else {
            false
        }
    }
}