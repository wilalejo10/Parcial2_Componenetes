package com.example.parcial2_componentes.domain.usecase

import com.example.parcial2_componentes.domain.model.Trip
import com.example.parcial2_componentes.domain.repository.TripRepository

class GetTripsUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(): Result<List<Trip>> = repository.getTrips()
}

class CreateTripUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(trip: Trip): Result<Trip> {
        if (trip.name.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre del viaje es obligatorio"))
        }
        if (trip.destination.isBlank()) {
            return Result.failure(IllegalArgumentException("El destino es obligatorio"))
        }
        if (trip.endDate.isBefore(trip.startDate)) {
            return Result.failure(IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio"))
        }
        return repository.createTrip(trip)
    }
}

class GetTripDetailUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(tripId: String): Result<Trip> = repository.getTripDetail(tripId)
}

class CalculateTotalExpensesUseCase {
    operator fun invoke(trip: Trip): Double = trip.totalExpenses
}