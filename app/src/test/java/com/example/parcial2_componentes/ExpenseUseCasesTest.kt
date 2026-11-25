package com.example.parcial2_componentes

import com.example.parcial2_componentes.data.repository.CategoryRepositoryImpl
import com.example.parcial2_componentes.data.repository.ExpenseRepositoryImpl
import com.example.parcial2_componentes.domain.model.Category
import com.example.parcial2_componentes.domain.model.Expense
import com.example.parcial2_componentes.domain.model.Trip
import com.example.parcial2_componentes.domain.usecase.AddExpenseUseCase
import com.example.parcial2_componentes.domain.usecase.GetCategoriesUseCase
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.LocalDate

class ExpenseUseCasesTest {

    private val sampleTrip = Trip(
        id = "1",
        name = "Europa 2026",
        destination = "Italia",
        startDate = LocalDate.of(2026, 6, 10),
        endDate = LocalDate.of(2026, 6, 20)
    )

    @Test
    fun `obtener categorias devuelve lista no vacia`() = runBlocking {
        val useCase = GetCategoriesUseCase(CategoryRepositoryImpl())
        val result = useCase()
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isNotEmpty())
    }

    @Test
    fun `registrar gasto valido tiene exito`() = runBlocking {
        val useCase = AddExpenseUseCase(ExpenseRepositoryImpl())
        val expense = Expense(
            tripId = "1",
            description = "Cena en Trastevere",
            amount = 28.50,
            category = Category("1", "Alimentación"),
            date = LocalDate.of(2026, 6, 12)
        )
        val result = useCase(sampleTrip, expense)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `registrar gasto falla cuando el valor es negativo o cero`() = runBlocking {
        val useCase = AddExpenseUseCase(ExpenseRepositoryImpl())
        val expense = Expense(
            tripId = "1",
            description = "Café",
            amount = 0.0,
            category = Category("1", "Alimentación"),
            date = LocalDate.of(2026, 6, 12)
        )
        val result = useCase(sampleTrip, expense)
        assertTrue(result.isFailure)
    }

    @Test
    fun `registrar gasto falla cuando la fecha esta fuera del rango del viaje`() = runBlocking {
        val useCase = AddExpenseUseCase(ExpenseRepositoryImpl())
        val expense = Expense(
            tripId = "1",
            description = "Hotel",
            amount = 150.0,
            category = Category("2", "Alojamiento"),
            date = LocalDate.of(2026, 6, 25) // Posterior al endDate (2026-06-20)
        )
        val result = useCase(sampleTrip, expense)
        assertTrue(result.isFailure)
    }

    @Test
    fun `registrar gasto falla cuando falta descripcion obligatoria`() = runBlocking {
        val useCase = AddExpenseUseCase(ExpenseRepositoryImpl())
        val expense = Expense(
            tripId = "1",
            description = "   ",
            amount = 20.0,
            category = Category("1", "Alimentación"),
            date = LocalDate.of(2026, 6, 15)
        )
        val result = useCase(sampleTrip, expense)
        assertTrue(result.isFailure)
    }
}