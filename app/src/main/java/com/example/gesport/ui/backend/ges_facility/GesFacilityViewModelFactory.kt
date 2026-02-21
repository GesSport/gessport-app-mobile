package com.example.gesport.ui.backend.ges_facility

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gesport.data.RoomFacilityRepository
import com.example.gesport.database.AppDatabase

class GesFacilityViewModelFactory(
    private val appContext: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(appContext)
        val facilityDao = database.facilityDao()

        val repo = RoomFacilityRepository(facilityDao)
        return GesFacilityViewModel(repo) as T
    }
}
