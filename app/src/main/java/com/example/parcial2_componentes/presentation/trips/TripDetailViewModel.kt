package com.example.parcial2_componentes.presentation.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2_componentes.domain.model.Trip
import com.example.parcial2_componentes.domain.usecase.GetTripDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TripDetailUiState(
    val isLoading: Boolean = false,
    val trip: Trip? = null,
    val error: String? = null
)

class TripDetailViewModel(
    private val getTripDetailUseCase: GetTripDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripDetailUiState())
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    fun loadTripDetail(tripId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getTripDetailUseCase(tripId)
                .onSuccess { trip ->
                    _uiState.value = TripDetailUiState(isLoading = false, trip = trip)
                }
                .onFailure { e ->
                    _uiState.value = TripDetailUiState(isLoading = false, error = e.message)
                }
        }
    }
}