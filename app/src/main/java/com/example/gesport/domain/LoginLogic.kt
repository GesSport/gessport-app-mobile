package com.example.gesport.domain

import com.example.gesport.data.LoginRepository
import com.example.gesport.models.User

class LoginLogic(
) {
    /* Dejamos aquí la lógica */

    fun checkLogin(email: String, password: String): User {
        if (email.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("Los campos no pueden estar vacíos.")
        }

        val user = LoginRepository.getUsers()
            // Se puede hacer con un foreach o con lamda
            // it = iterador (valor con el que se está trabajando)

            .find { it.email == email && it.password == password }
            ?: throw IllegalArgumentException("Email o contraseña incorrectos.")

        return user
    }

    private val nameRegex = Regex("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]{4,}$")
    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val passRegex = Regex("^(?=.*[a-z])(?=.*[A-Z]).{6,}$")


    fun validateName(name: String) {
        val v = name.trim()
        if (v.isEmpty()) throw IllegalArgumentException("El nombre no puede estar vacío.")
        if (!nameRegex.matches(v)) {
            throw IllegalArgumentException("El nombre debe tener mínimo 4 letras (solo letras y espacios).")
        }
    }

    fun validateEmail(email: String) {
        val v = email.trim()
        if (v.isEmpty()) throw IllegalArgumentException("El correo electrónico no puede estar vacío.")
        if (!emailRegex.matches(v)) {
            throw IllegalArgumentException("El correo electrónico no es válido.")
        }
    }

    /**
     * Longitud exacta del teléfono (9 dígitos )
     */
    fun validatePhone(phone: String, requiredLength: Int = 9) {
        val v = phone.trim()
        if (v.isEmpty()) throw IllegalArgumentException("El teléfono no puede estar vacío.")
        val phoneRegex = Regex("^\\d{$requiredLength}$")
        if (!phoneRegex.matches(v)) {
            throw IllegalArgumentException("El teléfono debe tener $requiredLength números.")
        }
    }

    fun validatePassword(password: String) {
        if (password.isEmpty()) throw IllegalArgumentException("La contraseña no puede estar vacía.")
        if (!passRegex.matches(password)) {
            throw IllegalArgumentException("La contraseña debe tener mayúscula, minúscula y al menos 6 caracteres.")
        }
    }

    fun validateRepeat(password: String, repeatPassword: String) {
        if (repeatPassword.isEmpty()) throw IllegalArgumentException("Repite la contraseña.")
        if (password != repeatPassword) throw IllegalArgumentException("Las contraseñas no coinciden.")
    }

}
