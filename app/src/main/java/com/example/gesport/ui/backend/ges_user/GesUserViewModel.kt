package com.example.gesport.ui.backend.ges_user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gesport.models.User
import com.example.gesport.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GesUserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    // Lista base (la que viene de Room sin filtros)
    private var _allUsers by mutableStateOf<List<User>>(emptyList())

    // Lista filtrada (la que consume la UI)
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

    // Para cancelar y re-suscribirse cuando cambie el rol
    private var usersJob: Job? = null

    init {
        // Al iniciar, suscripción a todos los usuarios
        observeUsers(role = null)
    }

    /**
     * Observa el Flow de Room.
     * Si role == null -> getAllUsers()
     * Si role != null -> getUsersByRole(role)
     */
    private fun observeUsers(role: String?) {
        usersJob?.cancel()
        usersJob = viewModelScope.launch {
            try {
                _isLoading = true
                _errorMessage = null

                val flow = if (role == null) {
                    userRepository.getAllUsers()
                } else {
                    userRepository.getUsersByRole(role)
                }

                flow.collectLatest { list ->
                    _allUsers = list
                    applyFilters()
                    _isLoading = false
                }
            } catch (e: Exception) {
                _errorMessage = e.message ?: "Error al cargar los usuarios"
                _allUsers = emptyList()
                _users = emptyList()
                _isLoading = false
            }
        }
    }

    /** Aplica SOLO la búsqueda (y el rol ya viene filtrado por el Flow si está seleccionado). */
    private fun applyFilters() {
        var filtered = _allUsers

        // búsqueda por nombre/email
        val q = _searchQuery.trim().lowercase()
        if (q.isNotEmpty()) {
            filtered = filtered.filter { user ->
                user.nombre.lowercase().contains(q) ||
                        user.email.lowercase().contains(q)
            }
        }

        _users = filtered
    }

    /** Cambiar rol desde chips */
    fun onRoleSelected(rol: String?) {
        _selectedRole = rol
        observeUsers(role = rol)
    }

    /** Cambiar texto de búsqueda */
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery = newQuery
        applyFilters()
    }

    /** Crear usuario */
    fun addUser(user: User) {
        viewModelScope.launch {
            try {
                _errorMessage = null
                userRepository.addUser(user)
                // No hace falta refresh: Flow se actualiza solo
            } catch (e: Exception) {
                _errorMessage = e.message ?: "No se ha podido crear el usuario"
            }
        }
    }

    /** Actualizar usuario */
    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                _errorMessage = null
                val rows = userRepository.updateUser(user)
                if (rows <= 0) {
                    _errorMessage = "Este usuario ya no existe"
                }
                // Flow actualiza automáticamente
            } catch (e: Exception) {
                _errorMessage = e.message ?: "No se ha podido actualizar el usuario"
            }
        }
    }

    /** Eliminar usuario */
    fun deleteUser(id: Int) {
        viewModelScope.launch {
            try {
                _errorMessage = null
                val ok = userRepository.deleteUser(id)
                if (!ok) {
                    _errorMessage = "No se ha podido borrar el usuario"
                }
                // Flow actualiza automáticamente
            } catch (e: Exception) {
                _errorMessage = e.message ?: "No se ha podido borrar el usuario"
            }
        }
    }

    /** Obtener un usuario para editar */
    fun loadUserById(id: Int, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserById(id)
                onResult(user)
            } catch (_: Exception) {
                onResult(null)
            }
        }
    }
}
