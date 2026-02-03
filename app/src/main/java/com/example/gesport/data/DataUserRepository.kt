package com.example.gesport.data

/*

import com.example.gesport.models.User
import com.example.gesport.repository.UserRepository

// Singleton
object DataUserRepository : UserRepository {

    private val users = mutableListOf(
        User(
            id = 1,
            name = "Ana Pérez",
            email = "ana.admin@club.es",
            password = "1234",
            rol = "ADMIN_DEPORTIVO"
        ),
        User(
            id = 2,
            name = "Pedro Caselles",
            email = "pedro.entrenador@club.es",
            password = "1234",
            rol ="ENTRENADOR"
        ),
        User(
            id = 3,
            name = "Pepa Ferrández",
            email = "pepa.jugadora@club.es",
            password = "1234",
            rol = "JUGADOR"
        ),
        User(
            id = 4,
            name = "Pablo Teruel",
            email = "pablo.arbitro@club.es",
            password = "1234",
            rol = "ARBITRO"
        ),
        User(
            id = 5,
            name = "María Belmonte",
            email = "maria.jugadora@club.es",
            password = "1234",
            rol = "JUGADOR"
        )
    )

    private fun getNewId(): Int {
        return (users.maxOfOrNull { it.id } ?: 0) + 1
    }

    override suspend fun addUser(user: User): User {
        val newId = getNewId()
        val newUser = user.copy(id = newId)
        users.add(newUser)
        return newUser
    }

    override suspend fun getUserById(id: Int): User? {
        return users.find { it.id == id }
    }

    override suspend fun updateUser(user: User): Boolean {
        val index = users.indexOfFirst { it.id == user.id }
        return if (index != -1) {
            users[index] = user
            true
        } else {
            false
        }
    }

    override suspend fun deleteUser(id: Int): Boolean {
        val user = users.find { it.id == id } ?: return false
        users.remove(user)
        return true
    }

    override suspend fun getAllUsers(): List<User> {
        return users.toList()
    }

    override suspend fun getUsersByRole(rol: String): List<User> =
        users.filter { it.rol == rol }
}

 */
