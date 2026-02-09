package com.example.gesport.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.gesport.models.Facility

@Dao
interface FacilityDao {

    // ==================== INSERTAR ====================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(facility: Facility): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(facilities: List<Facility>)

    // ==================== ACTUALIZAR ====================
    @Update
    suspend fun update(facility: Facility)

    // ==================== ELIMINAR ====================
    @Delete
    suspend fun delete(facility: Facility)

    @Query("DELETE FROM instalaciones WHERE id = :facilityId")
    suspend fun deleteById(facilityId: Int)

    @Query("DELETE FROM instalaciones")
    suspend fun deleteAll()

    // ==================== OBTENER TODAS ====================
    @Query("SELECT * FROM instalaciones ORDER BY nombre ASC")
    fun getAllFacilities(): LiveData<List<Facility>>

    @Query("SELECT * FROM instalaciones ORDER BY nombre ASC")
    suspend fun getAllFacilitiesSync(): List<Facility>

    // ==================== OBTENER POR ID ====================
    @Query("SELECT * FROM instalaciones WHERE id = :facilityId")
    suspend fun getFacilityById(facilityId: Int): Facility?

    @Query("SELECT * FROM instalaciones WHERE id = :facilityId")
    fun getFacilityByIdLiveData(facilityId: Int): LiveData<Facility?>

    // ==================== OBTENER POR TIPO DE DEPORTE ====================
    @Query("SELECT * FROM instalaciones WHERE tipoDeporte = :tipoDeporte ORDER BY nombre ASC")
    fun getFacilitiesBySport(tipoDeporte: String): LiveData<List<Facility>>

    @Query("SELECT * FROM instalaciones WHERE tipoDeporte = :tipoDeporte ORDER BY nombre ASC")
    suspend fun getFacilitiesBySportSync(tipoDeporte: String): List<Facility>

    // ==================== OBTENER DISPONIBLES ====================
    @Query("SELECT * FROM instalaciones WHERE disponible = 1 ORDER BY nombre ASC")
    fun getAvailableFacilities(): LiveData<List<Facility>>

    @Query("SELECT * FROM instalaciones WHERE disponible = 1 ORDER BY nombre ASC")
    suspend fun getAvailableFacilitiesSync(): List<Facility>

    @Query("SELECT * FROM instalaciones WHERE disponible = 1 AND tipoDeporte = :tipoDeporte ORDER BY nombre ASC")
    fun getAvailableFacilitiesBySport(tipoDeporte: String): LiveData<List<Facility>>

    @Query("SELECT * FROM instalaciones WHERE disponible = 1 AND tipoDeporte = :tipoDeporte ORDER BY nombre ASC")
    suspend fun getAvailableFacilitiesBySportSync(tipoDeporte: String): List<Facility>

    // ==================== OBTENER NO DISPONIBLES ====================
    @Query("SELECT * FROM instalaciones WHERE disponible = 0 ORDER BY nombre ASC")
    fun getUnavailableFacilities(): LiveData<List<Facility>>

    @Query("SELECT * FROM instalaciones WHERE disponible = 0 ORDER BY nombre ASC")
    suspend fun getUnavailableFacilitiesSync(): List<Facility>

    // ==================== CAMBIAR DISPONIBILIDAD ====================
    @Query("UPDATE instalaciones SET disponible = :disponible WHERE id = :facilityId")
    suspend fun updateAvailability(facilityId: Int, disponible: Boolean)

    @Query("UPDATE instalaciones SET disponible = 1 WHERE id = :facilityId")
    suspend fun markAsAvailable(facilityId: Int)

    @Query("UPDATE instalaciones SET disponible = 0 WHERE id = :facilityId")
    suspend fun markAsUnavailable(facilityId: Int)

    // ==================== VERIFICACIONES ====================
    @Query("SELECT COUNT(*) FROM instalaciones WHERE nombre = :nombre")
    suspend fun facilityNameExists(nombre: String): Int

    @Query("SELECT COUNT(*) FROM instalaciones WHERE nombre = :nombre AND id != :excludeId")
    suspend fun facilityNameExistsExcluding(nombre: String, excludeId: Int): Int

    @Query("SELECT COUNT(*) FROM instalaciones")
    suspend fun getFacilityCount(): Int

    @Query("SELECT COUNT(*) FROM instalaciones WHERE tipoDeporte = :tipoDeporte")
    suspend fun getFacilityCountBySport(tipoDeporte: String): Int

    @Query("SELECT COUNT(*) FROM instalaciones WHERE disponible = 1")
    suspend fun getAvailableFacilityCount(): Int

    // ==================== BÚSQUEDA ====================
    @Query("SELECT * FROM instalaciones WHERE nombre LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun searchFacilities(query: String): LiveData<List<Facility>>

    @Query("SELECT * FROM instalaciones WHERE nombre LIKE '%' || :query || '%' ORDER BY nombre ASC")
    suspend fun searchFacilitiesSync(query: String): List<Facility>

    // ==================== OBTENER POR CAPACIDAD ====================
    @Query("SELECT * FROM instalaciones WHERE capacidad >= :minCapacidad ORDER BY capacidad DESC")
    fun getFacilitiesByMinCapacity(minCapacidad: Int): LiveData<List<Facility>>

    @Query("SELECT * FROM instalaciones WHERE capacidad >= :minCapacidad ORDER BY capacidad DESC")
    suspend fun getFacilitiesByMinCapacitySync(minCapacidad: Int): List<Facility>

    // ==================== ESTADÍSTICAS ====================
    @Query("""
        SELECT tipoDeporte, COUNT(*) as count 
        FROM instalaciones 
        GROUP BY tipoDeporte 
        ORDER BY count DESC
    """)
    suspend fun getFacilityCountBySports(): Map<String, Int>

    @Query("SELECT AVG(capacidad) FROM instalaciones WHERE capacidad IS NOT NULL")
    suspend fun getAverageCapacity(): Double?
}
