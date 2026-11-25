package com.example.parcial2_componentes.presentation.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2_componentes.domain.model.Category
import com.example.parcial2_componentes.domain.model.Expense
import com.example.parcial2_componentes.domain.model.Trip
import com.example.parcial2_componentes.domain.usecase.AddExpenseUseCase
import com.example.parcial2_componentes.domain.usecase.GetCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AddExpenseUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val error: String? = null,
    val expenseSavedSuccess: Boolean = false
)

class AddExpenseViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addExpenseUseCase: AddExpenseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase()
                .onSuccess { list ->
                    _uiState.value = _uiState.value.copy(
                        categories = list,
                        selectedCategory = list.firstOrNull()
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
        }
    }

    fun selectCategory(category: Category) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun saveExpense(trip: Trip, description: String, amountText: String, date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, expenseSavedSuccess = false)

            val amount = amountText.replace(",", ".").toDoubleOrNull() ?: -1.0
            val category = _uiState.value.selectedCategory ?: Category("", "")

            val expense = Expense(
                tripId = trip.id,
                description = description,
                amount = amount,
                category = category,
                date = date
            )

            addExpenseUseCase(trip, expense)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, expenseSavedSuccess = true)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
        }
    }
}