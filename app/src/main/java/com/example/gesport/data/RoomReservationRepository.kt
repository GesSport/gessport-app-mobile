package com.example.gesport.data

import com.example.gesport.database.ReservationDao
import com.example.gesport.models.Reservation
import com.example.gesport.repository.ReservationRepository
import kotlinx.coroutines.flow.Flow

class RoomReservationRepository(
    private val reservationDao: ReservationDao
) : ReservationRepository {

    override fun getAllReservations(): Flow<List<Reservation>> =
        reservationDao.getAll()

    override suspend fun getReservationById(id: Int): Reservation? =
        reservationDao.getById(id)

    override suspend fun addReservation(reservation: Reservation): Reservation {
        val id = reservationDao.insert(reservation)
        return reservation.copy(id = id.toInt())
    }

    override suspend fun updateReservation(reservation: Reservation): Int =
        reservationDao.update(reservation)

    override suspend fun deleteReservation(id: Int): Boolean {
        val res = reservationDao.getById(id)
        return if (res != null) {
            reservationDao.delete(res)
            true
        } else false
    }

    override fun getReservationsByDateAndFacility(date: String, facilityId: Int): Flow<List<Reservation>> =
        reservationDao.getByDateAndFacility(date, facilityId)

    override fun getReservationsByUser(userId: Int): Flow<List<Reservation>> =
        reservationDao.getByUser(userId)

    override fun getReservationsByTeam(teamId: Int): Flow<List<Reservation>> =
        reservationDao.getByTeam(teamId)

    override fun getReservationsForTrainer(trainerId: Int): Flow<List<Reservation>> =
        reservationDao.getReservationsForTrainer(trainerId)
}