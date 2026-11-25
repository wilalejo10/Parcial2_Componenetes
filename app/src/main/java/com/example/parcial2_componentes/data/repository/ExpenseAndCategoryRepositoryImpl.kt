package com.example.parcial2_componentes.data.repository

import com.example.parcial2_componentes.domain.model.Category
import com.example.parcial2_componentes.domain.model.Expense
import com.example.parcial2_componentes.domain.repository.CategoryRepository
import com.example.parcial2_componentes.domain.repository.ExpenseRepository

class CategoryRepositoryImpl(
) : CategoryRepository {
    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            val categories = listOf(
                Category("1", "Alimentación"),
                Category("2", "Alojamiento"),
                Category("3", "Transporte"),
                Category("4", "Actividades"),
                Category("5", "Otros")
            )
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class ExpenseRepositoryImpl(
) : ExpenseRepository {

    private val expensesCache = mutableListOf<Expense>()

    override suspend fun addExpense(expense: Expense): Result<Expense> {
        return try {

            val saved = expense.copy(id = (expensesCache.size + 1).toString())
            expensesCache.add(saved)
            Result.success(saved)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}