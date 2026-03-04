package com.example.gesport.repository

import com.example.gesport.models.Facility
import kotlinx.coroutines.flow.Flow

interface FacilityRepository {

    // ROOM + Compose: Flow para observar cambios en tiempo real
    fun getAllFacilities(): Flow<List<Facility>>
    fun getFacilitiesBySport(sport: String): Flow<List<Facility>>

    fun searchFacilitiesByName(query: String): Flow<List<Facility>>

    /** Obtener instalación por id */
    suspend fun getFacilityById(id: Int): Facility?

    /** CRUD */
    suspend fun addFacility(facility: Facility): Facility
    suspend fun updateFacility(facility: Facility): Int
    suspend fun deleteFacility(id: Int): Boolean
}
