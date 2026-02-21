package com.example.gesport.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gesport.models.Facility
import kotlinx.coroutines.flow.Flow

@Dao
interface FacilityDao {

    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(facility: Facility): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(facilities: List<Facility>)

    // READ (listado principal)
    @Query("SELECT * FROM instalaciones ORDER BY nombre ASC")
    fun getAll(): Flow<List<Facility>>

    // READ (para editar)
    @Query("SELECT * FROM instalaciones WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Facility?

    // READ (filtro por deporte)
    @Query("SELECT * FROM instalaciones WHERE tipoDeporte = :sport ORDER BY nombre ASC")
    fun getBySport(sport: String): Flow<List<Facility>>

    // READ (búsqueda por nombre)
    @Query("SELECT * FROM instalaciones WHERE nombre LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun searchByName(query: String): Flow<List<Facility>>

    // UPDATE
    @Update
    suspend fun update(facility: Facility): Int

    // DELETE
    @Delete
    suspend fun delete(facility: Facility)

    @Query("DELETE FROM instalaciones WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM instalaciones")
    suspend fun deleteAll()
}
