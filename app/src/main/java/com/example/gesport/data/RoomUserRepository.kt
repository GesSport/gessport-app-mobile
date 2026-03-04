package com.example.gesport.data

import com.example.gesport.database.UserDao
import com.example.gesport.models.User
import com.example.gesport.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class RoomUserRepository(private val userDao: UserDao) : UserRepository {

    override fun getAllUsers(): Flow<List<User>> =
        userDao.getAll()

    override fun getUsersByRole(role: String): Flow<List<User>> =
        userDao.getByRole(role)

    override suspend fun getUserByEmail(email: String): User? =
        userDao.getByEmail(email)

    override fun getUsersByTeamId(teamId: Int): Flow<List<User>> =
        userDao.getByTeamId(teamId)

    override suspend fun clearTeamFromUsers(teamId: Int): Int =
        userDao.clearTeamFromUsers(teamId)

    /** Obtener un usuario por id */
    override suspend fun getUserById(id: Int): User? =
        userDao.getById(id)

    /** Crear usuario nuevo */
    override suspend fun addUser(user: User): User {
        val id = userDao.insert(user)
        return user.copy(id = id.toInt())
    }

    /** Actualizar usuario existente */
    override suspend fun updateUser(user: User): Int {
        val numActualizado = userDao.update(user)
        return numActualizado
    }

    /** Eliminar usuario por id */
    override suspend fun deleteUser(id: Int): Boolean {
        val user = userDao.getById(id)
        return if (user != null) {
            userDao.delete(user)
            true
        } else {
            false
        }
    }
}
