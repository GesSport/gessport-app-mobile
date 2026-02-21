package com.example.gesport.ui.backend.ges_team

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gesport.data.RoomTeamRepository
import com.example.gesport.data.RoomUserRepository
import com.example.gesport.database.AppDatabase

class GesTeamViewModelFactory(
    private val appContext: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(appContext)

        val teamDao = database.teamDao()
        val userDao = database.userDao()

        val teamRepo = RoomTeamRepository(teamDao)
        val userRepo = RoomUserRepository(userDao)

        return GesTeamViewModel(teamRepo, userRepo) as T
    }
}