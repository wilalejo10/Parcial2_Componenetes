package com.example.parcial2_componentes.domain.model

import java.time.LocalDate

data class Trip(
    val id: String = "",
    val name: String,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val expenses: List<Expense> = emptyList()
) {
    val totalExpenses: Double
        get() = expenses.sumOf { it.amount }
}

data class Expense(
    val id: String = "",
    val tripId: String,
    val description: String,
    val amount: Double,
    val category: Category,
    val date: LocalDate
)

data class Category(
    val id: String,
    val name: String
)