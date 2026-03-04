package com.example.gesport.ui.backend.ges_team

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gesport.models.Team
import com.example.gesport.models.User
import com.example.gesport.models.UserRoles
import com.example.gesport.repository.TeamRepository
import com.example.gesport.repository.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GesTeamViewModel(
    private val teamRepository: TeamRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // ===================== TEAMS =====================
    private var _allTeams by mutableStateOf<List<Team>>(emptyList())

    private var _teams by mutableStateOf<List<Team>>(emptyList())
    val teams: List<Team> get() = _teams

    private var _selectedCategory by mutableStateOf<String?>(null)
    val selectedCategory: String? get() = _selectedCategory

    private var _searchQuery by mutableStateOf("")
    val searchQuery: String get() = _searchQuery

    private var _isLoading by mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading

    private var _errorMessage by mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage

    private var teamsJob: Job? = null

    // ===================== TRAINERS =====================
    private var _trainers by mutableStateOf<List<User>>(emptyList())
    val trainers: List<User> get() = _trainers

    private var trainersJob: Job? = null

    init {
        observeTeams(category = null)
        observeTrainers()
    }

    // ===================== OBSERVE TEAMS =====================
    private fun observeTeams(category: String?) {
        teamsJob?.cancel()

        teamsJob = viewModelScope.launch {
            _isLoading = true
            _errorMessage = null

            try {
                val flow = if (category == null) {
                    teamRepository.getAllTeams()
                } else {
                    teamRepository.getTeamsByCategory(category)
                }

                flow.collectLatest { list ->
                    _allTeams = list
                    applyFilters()
                    _isLoading = false
                }
            } catch (e: CancellationException) {
                _isLoading = false
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "Error al cargar los equipos"
                _allTeams = emptyList()
                _teams = emptyList()
                _isLoading = false
            }
        }
    }

    private fun applyFilters() {
        var filtered = _allTeams

        val q = _searchQuery.trim().lowercase()
        if (q.isNotEmpty()) {
            filtered = filtered.filter { team ->
                team.nombre.lowercase().contains(q)
            }
        }

        _teams = filtered
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory = category
        observeTeams(category = category)
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery = newQuery
        applyFilters()
    }

    // ===================== OBSERVE TRAINERS =====================
    private fun observeTrainers() {
        trainersJob?.cancel()

        trainersJob = viewModelScope.launch {
            try {
                userRepository
                    .getUsersByRole(UserRoles.ENTRENADOR)
                    .collectLatest { list ->
                        _trainers = list
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _trainers = emptyList()
            }
        }
    }

    // ===================== HELPERS SYNC =====================

    private suspend fun assignTrainerToTeam(trainerId: Int, teamId: Int) {
        val trainer = userRepository.getUserById(trainerId) ?: return

        if (trainer.rol != UserRoles.ENTRENADOR) return

        if (trainer.equipoId == teamId) return

        userRepository.updateUser(
            trainer.copy(
                equipoId = teamId,
                // Posición solo aplica a jugador
                posicion = null
            )
        )
    }

    private suspend fun unassignTrainerFromTeamIfMatches(trainerId: Int, teamId: Int) {
        val trainer = userRepository.getUserById(trainerId) ?: return
        if (trainer.rol != UserRoles.ENTRENADOR) return

        // Solo lo quitamos si estaba asignado a ESTE equipo
        if (trainer.equipoId != teamId) return

        userRepository.updateUser(
            trainer.copy(
                equipoId = null,
                posicion = null
            )
        )
    }

    // ===================== CRUD =====================

    fun addTeam(team: Team) {
        viewModelScope.launch {
            try {
                _errorMessage = null

                // Crear equipo para obtener ID
                val created = teamRepository.addTeam(team)

                // Si hay entrenador, sincronizamos su equipoId
                val trainerId = created.entrenadorId
                if (trainerId != null) {
                    assignTrainerToTeam(trainerId = trainerId, teamId = created.id)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "No se ha podido crear el equipo"
            }
        }
    }

    fun updateTeam(team: Team) {
        viewModelScope.launch {
            try {
                _errorMessage = null

                // Cargar estado previo para detectar cambios de entrenador
                val old = teamRepository.getTeamById(team.id)
                val oldTrainerId = old?.entrenadorId
                val newTrainerId = team.entrenadorId

                val rows = teamRepository.updateTeam(team)
                if (rows <= 0) {
                    _errorMessage = "Este equipo ya no existe"
                    return@launch
                }

                // Si cambió el entrenador, sincronizamos:
                if (oldTrainerId != newTrainerId) {
                    // Quitar al anterior (si estaba en este equipo)
                    if (oldTrainerId != null) {
                        unassignTrainerFromTeamIfMatches(trainerId = oldTrainerId, teamId = team.id)
                    }
                    // Poner al nuevo
                    if (newTrainerId != null) {
                        assignTrainerToTeam(trainerId = newTrainerId, teamId = team.id)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "No se ha podido actualizar el equipo"
            }
        }
    }

    fun deleteTeam(id: Int) {
        viewModelScope.launch {
            try {
                _errorMessage = null

                // 1) Limpiar a todos los usuarios que pertenezcan a este equipo (jugadores + entrenador)
                //    (equipoId = null y posicion = null)
                userRepository.clearTeamFromUsers(id)

                // 2) Borrar equipo
                val ok = teamRepository.deleteTeam(id)
                if (!ok) {
                    _errorMessage = "No se ha podido borrar el equipo"
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "No se ha podido borrar el equipo"
            }
        }
    }

    fun loadTeamById(id: Int, onResult: (Team?) -> Unit) {
        viewModelScope.launch {
            try {
                val team = teamRepository.getTeamById(id)
                onResult(team)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                onResult(null)
            }
        }
    }
}