package com.example.gesport.ui.backend.ges_user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gesport.models.User
import com.example.gesport.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * ViewModel responsable de la gestión de usuarios para la parte de backend (GesUser).
 *
 * Se encarga de:
 * - Cargar la lista de usuarios desde el repositorio.
 * - Mantener en memoria la lista completa y la lista filtrada.
 * - Aplicar filtros por rol y por texto de búsqueda.
 * - Crear, actualizar y eliminar usuarios.
 * - Exponer estados de carga y error para que la UI los muestre.
 */
class GesUserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    // Lista completa desde el repositorio
    private var _allUsers by mutableStateOf<List<User>>(emptyList())

    // Lista filtrada que ve la UI
    private var _users by mutableStateOf<List<User>>(emptyList())
    val users: List<User> get() = _users

    // Rol seleccionado (null = todos)
    private var _selectedRole by mutableStateOf<String?>(null)
    val selectedRole: String? get() = _selectedRole

    // Texto de búsqueda
    private var _searchQuery by mutableStateOf("")
    val searchQuery: String get() = _searchQuery

    // Estados de carga y error
    private var _isLoading by mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading

    private var _errorMessage by mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage

    init {
        // Al crear el ViewModel se carga la lista inicial de usuarios
        refreshUsers()
    }

    /** Carga la lista base desde el repositorio y aplica filtros/búsqueda */
    fun refreshUsers() {
        viewModelScope.launch {
            try {
                _isLoading = true
                _errorMessage = null

                // Obtiene todos los usuarios desde el repositorio
                _allUsers = userRepository.getAllUsers()
                // Aplica filtros activos (rol + búsqueda) sobre la lista completa
                applyFilters()
            } catch (e: Exception) {
                _errorMessage = e.message ?: "Error al cargar los usuarios"
                _allUsers = emptyList()
                _users = emptyList()
            } finally {
                _isLoading = false
            }
        }
    }

    /** Aplica rol + búsqueda sobre la lista base */
    private fun applyFilters() {
        var filtered = _allUsers

        // Filtro por rol
        _selectedRole?.let { role ->
            filtered = filtered.filter { it.rol == role }
        }

        // Filtro por búsqueda (nombre o email)
        val query = _searchQuery.trim()
        if (query.isNotEmpty()) {
            val q = query.lowercase()
            filtered = filtered.filter { user ->
                user.name.lowercase().contains(q) ||
                        user.email.lowercase().contains(q)
            }
        }

        // Actualiza la lista que consume directamente la UI
        _users = filtered
    }

    /** Cambiar rol desde los chips */
    fun onRoleSelected(rol: String?) {
        _selectedRole = rol
        applyFilters()
    }

    /** Cambiar texto de búsqueda desde la barra */
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery = newQuery
        applyFilters()
    }

    /** Crear usuario nuevo */
    fun addUser(user: User) {
        viewModelScope.launch {
            try {
                _isLoading = true
                _errorMessage = null

                // Inserta el nuevo usuario en el repositorio
                userRepository.addUser(user)

                // Recarga la lista completa tras la inserción y vuelve a filtrar
                _allUsers = userRepository.getAllUsers()
                applyFilters()
            } catch (e: Exception) {
                _errorMessage = e.message ?: "No se ha podido crear el usuario"
            } finally {
                _isLoading = false
            }
        }
    }

    /** Actualizar usuario existente */
    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                _isLoading = true
                _errorMessage = null

                // Intenta actualizar el usuario en el repositorio
                val ok = userRepository.updateUser(user)
                if (!ok) {
                    _errorMessage = "Este usuario ya no existe"
                }

                // Recarga la lista completa tras la actualización y vuelve a filtrar
                _allUsers = userRepository.getAllUsers()
                applyFilters()
            } catch (e: Exception) {
                _errorMessage = e.message ?: "No se ha podido actualizar el usuario"
            } finally {
                _isLoading = false
            }
        }
    }

    /** Eliminar usuario por id */
    fun deleteUser(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading = true
                _errorMessage = null

                // Intenta eliminar el usuario en el repositorio
                val ok = userRepository.deleteUser(id)
                if (!ok) {
                    _errorMessage = "No se ha podido borrar el usuario"
                }

                // Recarga la lista completa tras el borrado y vuelve a filtrar
                _allUsers = userRepository.getAllUsers()
                applyFilters()
            } catch (e: Exception) {
                _errorMessage = e.message ?:"No se ha podido borrar el usuario"
            } finally {
                _isLoading = false
            }
        }
    }

    /** Obtener un usuario concreto para la pantalla de editar */
    fun loadUserById(
        id: Int,
        onResult: (User?) -> Unit
    ) {
        viewModelScope.launch {
            // Consulta el usuario por id y devuelve el resultado al callback
            val user = userRepository.getUserById(id)
            onResult(user)
        }
    }
}
