package com.example.parcial2_componentes.domain.repository

import com.example.parcial2_componentes.domain.model.Trip

interface TripRepository {
    suspend fun getTrips(): Result<List<Trip>>
    suspend fun createTrip(trip: Trip): Result<Trip>
    suspend fun getTripDetail(tripId: String): Result<Trip>
}