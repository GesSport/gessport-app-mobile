package com.example.gesport.ui.backend.ges_reservation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gesport.data.RoomFacilityRepository
import com.example.gesport.data.RoomReservationRepository
import com.example.gesport.data.RoomTeamRepository
import com.example.gesport.data.RoomUserRepository
import com.example.gesport.database.AppDatabase

class GesReservationViewModelFactory(
    private val appContext: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(appContext)

        val reservationDao = database.reservationDao()
        val facilityDao = database.facilityDao()
        val userDao = database.userDao()
        val teamDao = database.teamDao()

        val reservationRepo = RoomReservationRepository(reservationDao)
        val facilityRepo = RoomFacilityRepository(facilityDao)
        val userRepo = RoomUserRepository(userDao)
        val teamRepo = RoomTeamRepository(teamDao)

        return GesReservationViewModel(
            reservationRepo,
            facilityRepo,
            userRepo,
            teamRepo
        ) as T
    }
}