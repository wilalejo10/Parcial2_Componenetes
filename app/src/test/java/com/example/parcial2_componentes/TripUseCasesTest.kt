package com.example.parcial2_componentes

import com.example.parcial2_componentes.data.repository.TripRepositoryImpl
import com.example.parcial2_componentes.domain.model.Category
import com.example.parcial2_componentes.domain.model.Expense
import com.example.parcial2_componentes.domain.model.Trip
import com.example.parcial2_componentes.domain.usecase.CalculateTotalExpensesUseCase
import com.example.parcial2_componentes.domain.usecase.CreateTripUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class TripUseCasesTest {

    @Test
    fun `crear viaje exitosamente y listarlo`() = runBlocking {
        val repo = TripRepositoryImpl()
        val createUseCase = CreateTripUseCase(repo)
        val trip = Trip(
            name = "Europa 2026",
            destination = "Italia",
            startDate = LocalDate.of(2026, 6, 10),
            endDate = LocalDate.of(2026, 6, 20)
        )

        val result = createUseCase(trip)
        assertTrue(result.isSuccess)
        assertEquals(1, repo.getTrips().getOrNull()?.size)
    }

    @Test
    fun `crear viaje falla si la fecha fin es anterior a la fecha inicio`() = runBlocking {
        val repo = TripRepositoryImpl()
        val createUseCase = CreateTripUseCase(repo)
        val trip = Trip(
            name = "Viaje Inválido",
            destination = "Cartagena",
            startDate = LocalDate.of(2026, 6, 20),
            endDate = LocalDate.of(2026, 6, 10)
        )

        val result = createUseCase(trip)
        assertTrue(result.isFailure)
    }

    @Test
    fun `calcular total de gastos retorna la suma exacta`() {
        val calcUseCase = CalculateTotalExpensesUseCase()
        val trip = Trip(
            id = "1",
            name = "Europa 2026",
            destination = "Italia",
            startDate = LocalDate.of(2026, 6, 10),
            endDate = LocalDate.of(2026, 6, 20),
            expenses = listOf(
                Expense("1", "1", "Cena", 28.50, Category("1", "Alimentación"), LocalDate.of(2026, 6, 12)),
                Expense("2", "1", "Hotel", 260.00, Category("2", "Alojamiento"), LocalDate.of(2026, 6, 10))
            )
        )
        assertEquals(288.50, calcUseCase(trip), 0.01)
    }
}