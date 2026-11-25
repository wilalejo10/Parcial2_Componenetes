package com.example.parcial2_componentes.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.parcial2_componentes.domain.model.Category
import com.example.parcial2_componentes.domain.model.Expense
import com.example.parcial2_componentes.domain.model.Trip
import com.example.parcial2_componentes.domain.repository.CategoryRepository
import com.example.parcial2_componentes.domain.repository.ExpenseRepository

class GetCategoriesUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(): Result<List<Category>> = repository.getCategories()
}

class AddExpenseUseCase(private val repository: ExpenseRepository) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(trip: Trip, expense: Expense): Result<Expense> {

        if (expense.description.isBlank()) {
            return Result.failure(IllegalArgumentException("La descripción es obligatoria"))
        }
        if (expense.category.id.isBlank() || expense.category.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Debe seleccionar una categoría"))
        }


        if (expense.amount <= 0.0) {
            return Result.failure(IllegalArgumentException("El valor del gasto debe ser mayor a 0"))
        }


        if (expense.date.isBefore(trip.startDate) || expense.date.isAfter(trip.endDate)) {
            return Result.failure(
                IllegalArgumentException("La fecha debe estar entre ${trip.startDate} y ${trip.endDate}")
            )
        }

        return repository.addExpense(expense)
    }
}