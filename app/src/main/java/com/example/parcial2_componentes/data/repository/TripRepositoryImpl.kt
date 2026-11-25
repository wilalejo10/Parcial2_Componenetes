package com.example.parcial2_componentes.data.repository

import com.example.parcial2_componentes.domain.model.Trip
import com.example.parcial2_componentes.domain.repository.TripRepository

class TripRepositoryImpl : TripRepository {

    private val tripsCache = mutableListOf<Trip>()

    override suspend fun getTrips(): Result<List<Trip>> {
        return try {
            Result.success(tripsCache.toList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createTrip(trip: Trip): Result<Trip> {
        return try {
            val newTrip = trip.copy(id = (tripsCache.size + 1).toString())
            tripsCache.add(newTrip)
            Result.success(newTrip)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTripDetail(tripId: String): Result<Trip> {
        val trip = tripsCache.find { it.id == tripId }
        return if (trip != null) {
            Result.success(trip)
        } else {
            Result.failure(NoSuchElementException("Viaje no encontrado"))
        }
    }
}