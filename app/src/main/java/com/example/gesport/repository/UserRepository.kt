package com.example.gesport.repository

import com.example.gesport.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    // ROOM + Compose: Flow para observar cambios en tiempo real
    fun getAllUsers(): Flow<List<User>>
    fun getUsersByRole(role: String): Flow<List<User>>

    /*

    // ❌ (Modo antiguo sin Room / sin Flow)
    suspend fun getAllUsers(): List<User>
    suspend fun getUsersByRole(rol: String): List<User)

    */

    /** Obtener un usuario por email*/
    suspend fun getUserByEmail(email: String): User?

    /** Obtener un usuario por id */
    suspend fun getUserById(id: Int): User?


    /** CRUD */
    suspend fun addUser(user: User): User
    suspend fun updateUser(user: User): Int
    suspend fun deleteUser(id: Int): Boolean
}

/*
* PARA ROOM
* Room trabaja con Flow<List<User>> (para que Compose actualice automáticamente los datos al insertarse o borrarse usuarios).
*  fun getAllUsers(): Flow<List<User>>
    fun getUsersByRole(role: String): Flow<List<User>>
* */
