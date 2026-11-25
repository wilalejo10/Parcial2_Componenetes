package com.example.parcial2_componentes.domain.repository

import com.example.parcial2_componentes.domain.model.Expense

interface ExpenseRepository {
    suspend fun addExpense(expense: Expense): Result<Expense>
}