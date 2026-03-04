package com.example.gesport.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.gesport.models.User
import com.example.gesport.models.UserRoles
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    @Insert
    suspend fun insertUser(user: User)

    // READ
    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    fun getAll(): Flow<List<User>>

    @Query("SELECT * FROM usuarios WHERE rol = :role ORDER BY nombre ASC")
    fun getByRole(role: String): Flow<List<User>>
    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): User?

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): User?

    @Query("SELECT * FROM usuarios WHERE rol = :role LIMIT 1")
    suspend fun getAnyAdmin(role: String = UserRoles.ADMIN_DEPORTIVO): User?

    @Query("SELECT * FROM usuarios WHERE equipoId = :teamId ORDER BY nombre ASC")
    fun getByTeamId(teamId: Int): Flow<List<User>>

    // UPDATE
    @Update
    suspend fun update(user: User):Int

    @Query("UPDATE usuarios SET equipoId = NULL, posicion = NULL WHERE equipoId = :teamId")
    suspend fun clearTeamFromUsers(teamId: Int): Int

    // DELETE
    @Delete
    suspend fun delete(user: User)

    @Query("DELETE FROM usuarios")
    suspend fun deleteAll()
}

