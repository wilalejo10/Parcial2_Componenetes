package com.example.parcial2_componentes.presentation.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2_componentes.domain.model.Trip
import com.example.parcial2_componentes.domain.usecase.CreateTripUseCase
import com.example.parcial2_componentes.domain.usecase.GetTripsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TripListUiState(
    val isLoading: Boolean = false,
    val trips: List<Trip> = emptyList(),
    val error: String? = null,
    val tripCreatedSuccess: Boolean = false
)

class TripViewModel(
    private val getTripsUseCase: GetTripsUseCase,
    private val createTripUseCase: CreateTripUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripListUiState())
    val uiState: StateFlow<TripListUiState> = _uiState.asStateFlow()

    fun loadTrips() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getTripsUseCase()
                .onSuccess { list ->
                    _uiState.value = _uiState.value.copy(isLoading = false, trips = list)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
        }
    }

    fun createTrip(name: String, destination: String, startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, tripCreatedSuccess = false)
            val newTrip = Trip(name = name, destination = destination, startDate = startDate, endDate = endDate)
            createTripUseCase(newTrip)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, tripCreatedSuccess = true)
                    loadTrips()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
        }
    }

    fun resetCreateState() {
        _uiState.value = _uiState.value.copy(tripCreatedSuccess = false, error = null)
    }
}