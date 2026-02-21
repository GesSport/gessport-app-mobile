package com.example.gesport.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gesport.models.Team
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {

    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(team: Team): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(teams: List<Team>)

    // READ (listado principal)
    @Query("SELECT * FROM equipos ORDER BY nombre ASC")
    fun getAll(): Flow<List<Team>>

    // READ (para editar)
    @Query("SELECT * FROM equipos WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Team?

    // READ (filtro por categoría/deporte)
    @Query("SELECT * FROM equipos WHERE categoria = :category ORDER BY nombre ASC")
    fun getByCategory(category: String): Flow<List<Team>>

    // READ (filtro por entrenador)
    @Query("SELECT * FROM equipos WHERE entrenadorId = :trainerId ORDER BY nombre ASC")
    fun getByTrainer(trainerId: Int): Flow<List<Team>>

    // READ (búsqueda por nombre)
    @Query("SELECT * FROM equipos WHERE nombre LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun searchByName(query: String): Flow<List<Team>>

    // UPDATE
    @Update
    suspend fun update(team: Team): Int

    // DELETE
    @Delete
    suspend fun delete(team: Team)

    @Query("DELETE FROM equipos WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM equipos")
    suspend fun deleteAll()
}