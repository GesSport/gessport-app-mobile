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

    // ===================== CRUD =====================
    fun addTeam(team: Team) {
        viewModelScope.launch {
            try {
                _errorMessage = null
                teamRepository.addTeam(team)
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
                val rows = teamRepository.updateTeam(team)
                if (rows <= 0) {
                    _errorMessage = "Este equipo ya no existe"
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