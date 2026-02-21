package com.example.gesport.ui.backend.ges_reservation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gesport.models.Facility
import com.example.gesport.models.Reservation
import com.example.gesport.models.User
import com.example.gesport.repository.FacilityRepository
import com.example.gesport.repository.ReservationRepository
import com.example.gesport.repository.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GesReservationViewModel(
    private val reservationRepository: ReservationRepository,
    private val facilityRepository: FacilityRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // ================= FACILITIES =================
    private var _facilities by mutableStateOf<List<Facility>>(emptyList())
    val facilities: List<Facility> get() = _facilities
    private var facilitiesJob: Job? = null

    // ================= USERS =================
    private var _users by mutableStateOf<List<User>>(emptyList())
    val users: List<User> get() = _users
    private var usersJob: Job? = null

    // ================= RESERVATIONS (GRID: fecha + pista) =================
    private var _reservations by mutableStateOf<List<Reservation>>(emptyList())
    val reservations: List<Reservation> get() = _reservations
    private var reservationsJob: Job? = null

    // ================= RESERVATIONS (LISTADO: todas) =================
    private var _allReservations by mutableStateOf<List<Reservation>>(emptyList())
    val allReservations: List<Reservation> get() = _allReservations
    private var allReservationsJob: Job? = null

    // ================= FILTER STATE (GRID) =================
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
    }

    // ================= OBSERVE FACILITIES =================
    private fun observeFacilities() {
        facilitiesJob?.cancel()

        facilitiesJob = viewModelScope.launch {
            try {
                facilityRepository
                    .getAllFacilities()
                    .collectLatest { list ->
                        _facilities = list

                        // UX: si aún no hay instalación seleccionada, selecciona la primera
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
                userRepository
                    .getAllUsers()
                    .collectLatest { list ->
                        _users = list
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _users = emptyList()
            }
        }
    }

    // ================= HELPERS (UI) =================
    fun getFacilityNameById(id: Int): String? =
        _facilities.firstOrNull { it.id == id }?.nombre

    fun getUserNameById(id: Int): String? =
        _users.firstOrNull { it.id == id }?.nombre

    fun getUserEmailById(id: Int): String? =
        _users.firstOrNull { it.id == id }?.email

    fun getFacilityById(id: Int): Facility? =
        _facilities.firstOrNull { it.id == id }

    fun getUserById(id: Int): User? =
        _users.firstOrNull { it.id == id }

    // ================= LISTADO ADMIN =================
    fun loadAllReservations() {
        allReservationsJob?.cancel()

        allReservationsJob = viewModelScope.launch {
            _isLoading = true
            _errorMessage = null
            try {
                reservationRepository
                    .getAllReservations()
                    .collectLatest { list ->
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

    fun deleteReservation(id: Int) {
        viewModelScope.launch {
            try {
                _errorMessage = null
                val ok = reservationRepository.deleteReservation(id)
                if (!ok) _errorMessage = "No se ha podido borrar la reserva"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "No se ha podido borrar la reserva"
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

    // ================= CRUD (FORM) =================

    fun loadReservationById(id: Int, onLoaded: (Reservation?) -> Unit) {
        viewModelScope.launch {
            _errorMessage = null
            try {
                val res = reservationRepository.getReservationById(id)
                onLoaded(res)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "Error al cargar la reserva"
                onLoaded(null)
            }
        }
    }

    fun addReservation(reservation: Reservation, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _errorMessage = null
            try {
                val created = reservationRepository.addReservation(reservation)
                // Si el repo devuelve la propia reserva creada, asumimos ok si no es null
                onDone(created.id >= 0)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "No se ha podido crear la reserva"
                onDone(false)
            }
        }
    }

    fun updateReservation(reservation: Reservation, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _errorMessage = null
            try {
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

    /**
     * Validación simple basada en el estado actual del grid (fecha+pista).
     * Útil antes de guardar.
     */
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