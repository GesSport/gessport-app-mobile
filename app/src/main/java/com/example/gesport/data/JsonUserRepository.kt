package com.example.gesport.data

/*

import android.R.attr.text
import android.content.Context
import com.example.gesport.models.User
import com.example.gesport.repository.UserRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


class JsonUserRepository(private val context: Context): UserRepository {
    //private val jsonFile = File("res/assets/users.json")
    val jsonFile = context.assets.open("users.json")   //  nombre EXACTO del archivo
        .bufferedReader()
        .use { it.readText() }

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    private val users = mutableListOf<User>()
    private var nextId: Int = 1

    init {
        loadFromFile()
    }

    private fun loadFromFile() {
        if(jsonFile.isEmpty()) {
            users.clear()
            nextId = 1
            return
        }

        if (jsonFile.isBlank()) {
            users.clear()
            nextId = 1
            return
        }

        val loadedUsers : List<User> = json.decodeFromString(jsonFile)
        users.clear()
        users.addAll(loadedUsers)

        nextId = (users.maxOfOrNull { it.id } ?: 0) + 1
    }

    private fun saveToFile() {
        val text = json.encodeToString(users)
        // jsonFile.writeText(text)
    }

    private fun getNewId(): Int {
        return (users.maxOfOrNull { it.id } ?: 0) + 1
    }

    override suspend fun getAllUsers(): List<User> {
        return users.toList()
    }

    override suspend fun getUsersByRole(rol: String): List<User> {
        return users.filter { it.rol == rol }
    }

    override suspend fun getUserById(id: Int): User? {
        return users.find { it.id == id }
    }

    override suspend fun addUser(user: User): User {
        nextId = getNewId()
        val newUser = user.copy(id = nextId++)
        users.add(newUser)
        saveToFile()
        return  newUser
    }

    override suspend fun updateUser(user: User): Boolean {
        val index = users.indexOfFirst { it.id == user.id }
        if (index == 1) return false
        users[index] = user
        saveToFile()
        return true
    }

    override suspend fun deleteUser(id: Int): Boolean {
        val removed = users.removeIf { it.id == id }
        if (removed) {
            saveToFile()
        }
        return removed
    }
}

 */