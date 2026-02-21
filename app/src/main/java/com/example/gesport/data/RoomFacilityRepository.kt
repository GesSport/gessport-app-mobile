package com.example.gesport.data

import com.example.gesport.database.FacilityDao
import com.example.gesport.models.Facility
import com.example.gesport.repository.FacilityRepository
import kotlinx.coroutines.flow.Flow

class RoomFacilityRepository(private val facilityDao: FacilityDao) : FacilityRepository {

    override fun getAllFacilities(): Flow<List<Facility>> =
        facilityDao.getAll()

    override fun getFacilitiesBySport(sport: String): Flow<List<Facility>> =
        facilityDao.getBySport(sport)

    override fun searchFacilitiesByName(query: String): Flow<List<Facility>> =
        facilityDao.searchByName(query)

    override suspend fun getFacilityById(id: Int): Facility? =
        facilityDao.getById(id)

    override suspend fun addFacility(facility: Facility): Facility {
        val id = facilityDao.insert(facility)
        return facility.copy(id = id.toInt())
    }

    override suspend fun updateFacility(facility: Facility): Int {
        return facilityDao.update(facility)
    }

    override suspend fun deleteFacility(id: Int): Boolean {
        val facility = facilityDao.getById(id)
        return if (facility != null) {
            facilityDao.delete(facility)
            true
        } else {
            false
        }
    }
}
