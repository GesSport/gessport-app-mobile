package com.example.gesport.ui.backend.ges_facility

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gesport.models.Facility
import com.example.gesport.repository.FacilityRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GesFacilityViewModel(
    private val facilityRepository: FacilityRepository
) : ViewModel() {

    private var _allFacilities by mutableStateOf<List<Facility>>(emptyList())

    private var _facilities by mutableStateOf<List<Facility>>(emptyList())
    val facilities: List<Facility> get() = _facilities

    private var _selectedSport by mutableStateOf<String?>(null)
    val selectedSport: String? get() = _selectedSport

    private var _searchQuery by mutableStateOf("")
    val searchQuery: String get() = _searchQuery

    private var _isLoading by mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading

    private var _errorMessage by mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage

    private var facilitiesJob: Job? = null

    init {
        observeFacilities(sport = null)
    }

    private fun observeFacilities(sport: String?) {
        facilitiesJob?.cancel()

        facilitiesJob = viewModelScope.launch {
            _isLoading = true
            _errorMessage = null

            try {
                val flow = if (sport == null) {
                    facilityRepository.getAllFacilities()
                } else {
                    facilityRepository.getFacilitiesBySport(sport)
                }

                flow.collectLatest { list ->
                    _allFacilities = list
                    applyFilters()
                    _isLoading = false
                }
            } catch (e: CancellationException) {
                _isLoading = false
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "Error al cargar las instalaciones"
                _allFacilities = emptyList()
                _facilities = emptyList()
                _isLoading = false
            }
        }
    }

    private fun applyFilters() {
        var filtered = _allFacilities

        val q = _searchQuery.trim().lowercase()
        if (q.isNotEmpty()) {
            filtered = filtered.filter { facility ->
                facility.nombre.lowercase().contains(q)
            }
        }

        _facilities = filtered
    }

    fun onSportSelected(sport: String?) {
        _selectedSport = sport
        observeFacilities(sport = sport)
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery = newQuery
        applyFilters()
    }

    fun addFacility(facility: Facility) {
        viewModelScope.launch {
            try {
                _errorMessage = null
                facilityRepository.addFacility(facility)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "No se ha podido crear la instalación"
            }
        }
    }

    fun updateFacility(facility: Facility) {
        viewModelScope.launch {
            try {
                _errorMessage = null
                val rows = facilityRepository.updateFacility(facility)
                if (rows <= 0) _errorMessage = "Esta instalación ya no existe"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "No se ha podido actualizar la instalación"
            }
        }
    }

    fun deleteFacility(id: Int) {
        viewModelScope.launch {
            try {
                _errorMessage = null
                val ok = facilityRepository.deleteFacility(id)
                if (!ok) _errorMessage = "No se ha podido borrar la instalación"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage = e.message ?: "No se ha podido borrar la instalación"
            }
        }
    }

    fun loadFacilityById(id: Int, onResult: (Facility?) -> Unit) {
        viewModelScope.launch {
            try {
                val facility = facilityRepository.getFacilityById(id)
                onResult(facility)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                onResult(null)
            }
        }
    }
}