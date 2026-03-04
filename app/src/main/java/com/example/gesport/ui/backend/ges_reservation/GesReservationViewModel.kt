package com.example.gesport.ui.backend.ges_reservation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gesport.domain.ReservationAccess
import com.example.gesport.models.Facility
import com.example.gesport.models.Reservation
import com.example.gesport.models.Team
import com.example.gesport.models.User
import com.example.gesport.repository.FacilityRepository
import com.example.gesport.repository.ReservationRepository
import com.example.gesport.repository.TeamRepository
import com.example.gesport.repository.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GesReservationViewModel(
    private val reservationRepository: ReservationRepository,
    private val facilityRepository: FacilityRepository,
    private val userRepository: UserRepository,
    private val teamRepository: TeamRepository
) : ViewModel() {

    // ================= FACILITIES =================
    private var _facilities by mutableStateOf<List<Facility>>(emptyList())
    val facilities: List<Facility> get() = _facilities
    private var facilitiesJob: Job? = null

    // ================= USERS =================
    private var _users by mutableStateOf<List<User>>(emptyList())
    val users: List<User> get() = _users
    private var usersJob: Job? = null

    // ================= TEAMS =================
    private var _teams by mutableStateOf<List<Team>>(emptyList())
    val teams: List<Team> get() = _teams
    private var teamsJob: Job? = null

    // ================= RESERVATIONS (GRID fecha+pista) =================
    private var _reservations by mutableStateOf<List<Reservation>>(emptyList())
    val reservations: List<Reservation> get() = _reservations
    private var reservationsJob: Job? = null

    // ================= RESERVATIONS (LISTADO principal) =================
    private var _allReservations by mutableStateOf<List<Reservation>>(emptyList())
    val allReservations: List<Reservation> get() = _allReservations
    private var allReservationsJob: Job? = null

    // ================= FILTER STATE =================
    private var _selectedDate by mutableStateOf<String?>(null)
    val selectedDate: String? get() = _selectedDate

    private var _selectedFacilityId by mutableStateOf<Int?>(null)
    val selectedFacilityId: Int? get() = _selectedFacilityId

    // ================= UI STATE =================
    private var _isLoading by mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading

    private var _errorMessage by mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage

    init {
        observeFacilities()
        observeUsers()
        observeTeams()
    }

    // ================= OBSERVE FACILITIES =================
    private fun observeFacilities() {
        facilitiesJob?.cancel()
        facilitiesJob = viewModelScope.launch {
            try {
                facilityRepository.getAllFacilities().collectLatest { list ->
                    _facilities = list
                    if (_selectedFacilityId == null) {
                        _selectedFacilityId = list.firstOrNull()?.id
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _facilities = emptyList()
            }
        }
    }

    // ================= OBSERVE USERS =================
    private fun observeUsers() {
        usersJob?.cancel()
        usersJob = viewModelScope.launch {
            try {
                userRepository.getAllUsers().collectLatest { list ->
                    _users = list
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _users = emptyList()
            }
        }
    }

    // ================= OBSERVE TEAMS =================
    private fun observeTeams() {
        teamsJob?.cancel()
        teamsJob = viewModelScope.launch {
            try {
                teamRepository.getAllTeams().collectLatest { list ->
                    _teams = list
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _teams = emptyList()
            }
        }
    }

    // ================= HELPERS UI =================

    fun getFacilityNameById(id: Int): String? =
        _facilities.firstOrNull { it.id == id }?.nombre

    fun getFacilityById(id: Int): Facility? =
        _facilities.firstOrNull { it.id == id }

    fun getUserNameById(id: Int): String? =
        _users.firstOrNull { it.id == id }?.nombre

    fun getUserEmailById(id: Int): String? =
        _users.firstOrNull { it.id == id }?.email

    fun getUserById(id: Int): User? =
        _users.firstOrNull { it.id == id }

    fun getTeamNameById(id: Int): String? =
        _teams.firstOrNull { it.id == id }?.nombre

    fun getTeamById(id: Int): Team? =
        _teams.firstOrNull { it.id == id }

    private fun coachedTeamIdsOf(trainerId: Int): Set<Int> =
        _teams.filter { it.entrenadorId == trainerId }.map { it.id }.toSet()

    // ================= LISTADOS POR ROL =================

    // ADMIN
    fun loadAllReservations() {
        allReservationsJob?.cancel()
        allReservationsJob = viewModelScope.launch {
            _isLoading = true
            _errorMessage = null
            try {
                reservationRepository.getAllReservations().collectLatest { list ->
                    _allReservations = list
                    _isLoading = false
                }
            } catch (e: CancellationException) {
                _isLoading = false
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "Error al cargar reservas"
                _allReservations = emptyList()
                _isLoading = false
            }
        }
    }

    // JUGADOR: personales
    fun loadReservationsForUser(userId: Int) {
        allReservationsJob?.cancel()
        allReservationsJob = viewModelScope.launch {
            _isLoading = true
            _errorMessage = null
            try {
                reservationRepository.getReservationsByUser(userId).collectLatest { list ->
                    _allReservations = list
                    _isLoading = false
                }
            } catch (e: CancellationException) {
                _isLoading = false
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "Error al cargar reservas del usuario"
                _allReservations = emptyList()
                _isLoading = false
            }
        }
    }

    // ✅ ENTRENADOR: UNO solo y claro (repo -> DAO query)
    fun loadReservationsForTrainer(trainerId: Int) {
        allReservationsJob?.cancel()
        allReservationsJob = viewModelScope.launch {
            _isLoading = true
            _errorMessage = null
            try {
                reservationRepository.getReservationsForTrainer(trainerId).collectLatest { list ->
                    _allReservations = list
                    _isLoading = false
                }
            } catch (e: CancellationException) {
                _isLoading = false
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "Error al cargar reservas del entrenador"
                _allReservations = emptyList()
                _isLoading = false
            }
        }
    }

    // ================= GRID (fecha + pista) =================

    fun onDateSelected(date: String?) {
        _selectedDate = date
        observeReservationsByDateAndFacility()
    }

    fun onFacilitySelected(facilityId: Int?) {
        _selectedFacilityId = facilityId
        observeReservationsByDateAndFacility()
    }

    private fun observeReservationsByDateAndFacility() {
        val date = _selectedDate
        val facilityId = _selectedFacilityId

        if (date.isNullOrBlank() || facilityId == null) {
            _reservations = emptyList()
            return
        }

        reservationsJob?.cancel()
        reservationsJob = viewModelScope.launch {
            _isLoading = true
            _errorMessage = null
            try {
                reservationRepository
                    .getReservationsByDateAndFacility(date, facilityId)
                    .collectLatest { list ->
                        _reservations = list
                        _isLoading = false
                    }
            } catch (e: CancellationException) {
                _isLoading = false
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "Error al cargar reservas"
                _reservations = emptyList()
                _isLoading = false
            }
        }
    }

    // ================= CRUD (FORM) con permisos reales =================

    fun loadReservationById(id: Int, onLoaded: (Reservation?) -> Unit) {
        viewModelScope.launch {
            _errorMessage = null
            try {
                onLoaded(reservationRepository.getReservationById(id))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "Error al cargar la reserva"
                onLoaded(null)
            }
        }
    }

    fun addReservation(
        currentUserId: Int,
        currentUserRole: String,
        reservation: Reservation,
        onDone: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            _errorMessage = null
            try {
                val coachedIds = coachedTeamIdsOf(currentUserId)
                val allowed = ReservationAccess.canCreateOrUpdateReservation(
                    currentUserId = currentUserId,
                    currentUserRole = currentUserRole,
                    reservation = reservation,
                    coachedTeamIds = coachedIds
                )
                if (!allowed) {
                    _errorMessage = "No tienes permisos para crear esta reserva."
                    onDone(false)
                    return@launch
                }

                val created = reservationRepository.addReservation(reservation)
                onDone(created.id >= 0)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "No se ha podido crear la reserva"
                onDone(false)
            }
        }
    }

    fun updateReservation(
        currentUserId: Int,
        currentUserRole: String,
        reservation: Reservation,
        onDone: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            _errorMessage = null
            try {
                val existing = reservationRepository.getReservationById(reservation.id)
                if (existing == null) {
                    _errorMessage = "Esta reserva ya no existe."
                    onDone(false)
                    return@launch
                }

                val coachedIds = coachedTeamIdsOf(currentUserId)

                val canManageExisting = ReservationAccess.canManageReservation(
                    currentUserId = currentUserId,
                    currentUserRole = currentUserRole,
                    reservation = existing,
                    coachedTeamIds = coachedIds
                )
                if (!canManageExisting) {
                    _errorMessage = "No tienes permisos para editar esta reserva."
                    onDone(false)
                    return@launch
                }

                val canWriteNew = ReservationAccess.canCreateOrUpdateReservation(
                    currentUserId = currentUserId,
                    currentUserRole = currentUserRole,
                    reservation = reservation,
                    coachedTeamIds = coachedIds
                )
                if (!canWriteNew) {
                    _errorMessage = "No tienes permisos para aplicar estos cambios."
                    onDone(false)
                    return@launch
                }

                val rows = reservationRepository.updateReservation(reservation)
                val ok = rows > 0
                if (!ok) _errorMessage = "No se ha podido actualizar la reserva"
                onDone(ok)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "No se ha podido actualizar la reserva"
                onDone(false)
            }
        }
    }

    fun deleteReservation(
        currentUserId: Int,
        currentUserRole: String,
        reservationId: Int
    ) {
        viewModelScope.launch {
            try {
                _errorMessage = null

                val existing = reservationRepository.getReservationById(reservationId)
                if (existing == null) {
                    _errorMessage = "Esta reserva ya no existe."
                    return@launch
                }

                val coachedIds = coachedTeamIdsOf(currentUserId)
                val allowed = ReservationAccess.canManageReservation(
                    currentUserId = currentUserId,
                    currentUserRole = currentUserRole,
                    reservation = existing,
                    coachedTeamIds = coachedIds
                )
                if (!allowed) {
                    _errorMessage = "No tienes permisos para eliminar esta reserva."
                    return@launch
                }

                val ok = reservationRepository.deleteReservation(reservationId)
                if (!ok) _errorMessage = "No se ha podido borrar la reserva"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "No se ha podido borrar la reserva"
            }
        }
    }

    fun isSlotAvailable(
        start: String,
        end: String,
        ignoreReservationId: Int? = null
    ): Boolean {
        return _reservations.none { res ->
            if (ignoreReservationId != null && res.id == ignoreReservationId) return@none false
            overlaps(
                slotStart = start,
                slotEnd = end,
                resStart = res.horaInicio,
                resEnd = res.horaFin
            )
        }
    }
}