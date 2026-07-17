package com.rarcega.controlgastos.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rarcega.controlgastos.domain.model.Budget
import com.rarcega.controlgastos.domain.model.Category
import com.rarcega.controlgastos.domain.repository.BudgetRepository
import com.rarcega.controlgastos.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class BudgetViewModel(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())
    val budgets: StateFlow<List<Budget>> = _budgets

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _currentMonth = MutableStateFlow(
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).monthNumber
    )
    val currentMonth: StateFlow<Int> = _currentMonth

    private val _currentYear = MutableStateFlow(
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
    )
    val currentYear: StateFlow<Int> = _currentYear

    init {
        loadBudgets()
        loadCategories()
    }

    fun loadBudgets() {
        viewModelScope.launch {
            budgetRepository.getBudgetsByMonth(_currentMonth.value, _currentYear.value)
                .collect { _budgets.value = it }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { _categories.value = it }
        }
    }

    fun addBudget(categoryId: Long, categoryName: String, limitAmount: Double) {
        viewModelScope.launch {
            val budget = Budget(
                month = _currentMonth.value,
                year = _currentYear.value,
                categoryId = categoryId,
                categoryName = categoryName,
                limitAmount = limitAmount
            )
            budgetRepository.insertBudget(budget)
            loadBudgets()
        }
    }

    fun updateBudget(budget: Budget, newLimitAmount: Double) {
        viewModelScope.launch {
            budgetRepository.updateBudget(budget.copy(limitAmount = newLimitAmount))
            loadBudgets()
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            budgetRepository.deleteBudget(budget)
            loadBudgets()
        }
    }

    fun setMonth(month: Int, year: Int) {
        _currentMonth.value = month
        _currentYear.value = year
        loadBudgets()
    }
}
