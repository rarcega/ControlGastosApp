package com.rarcega.controlgastos.ui.dashboard

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rarcega.controlgastos.domain.model.MonthlySummary
import com.rarcega.controlgastos.domain.model.Transaction
import com.rarcega.controlgastos.domain.model.TransactionType
import com.rarcega.controlgastos.domain.repository.TransactionRepository
import com.rarcega.controlgastos.domain.repository.AccountRepository
import com.rarcega.controlgastos.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DashboardViewModel(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    private val _currentMonth = mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).monthNumber)
    private val _currentYear = mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year)

    private val _summary = MutableStateFlow(MonthlySummary(
        month = _currentMonth.value,
        year = _currentYear.value
    ))
    val summary: StateFlow<MonthlySummary> = _summary

    private val _recentTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val recentTransactions: StateFlow<List<Transaction>> = _recentTransactions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val month = _currentMonth.value
                val year = _currentYear.value

                val totalsFlow = combine(
                    transactionRepository.getTotalByTypeAndMonth(TransactionType.INCOME, month, year),
                    transactionRepository.getTotalByTypeAndMonth(TransactionType.SAVING, month, year),
                    transactionRepository.getTotalByTypeAndMonth(TransactionType.EXPENSE_FIXED, month, year),
                    transactionRepository.getTotalByTypeAndMonth(TransactionType.EXPENSE_VARIABLE, month, year),
                    transactionRepository.getTotalByTypeAndMonth(TransactionType.EXPENSE_CASH, month, year)
                ) { income, savings, fixedExpenses, variableExpenses, cashExpenses ->
                    val totalSpent = fixedExpenses + variableExpenses + cashExpenses
                    val initialBalance = 12.99
                    val expenseLimit = 1500.0
                    val finalBalance = initialBalance + income - totalSpent - savings
                    val benefit = income - totalSpent
                    val benefitPlusSavings = benefit + savings
                    val availableMargin = expenseLimit - totalSpent
                    val variablePercentage = if (totalSpent > 0) (variableExpenses / totalSpent) * 100 else 0.0

                    MonthlySummary(
                        month = month,
                        year = year,
                        initialBalance = initialBalance,
                        income = income,
                        savings = savings,
                        expenseLimit = expenseLimit,
                        fixedExpenses = fixedExpenses,
                        variableExpenses = variableExpenses,
                        cashExpenses = cashExpenses,
                        totalSpent = totalSpent,
                        finalBalance = finalBalance,
                        benefit = benefit,
                        benefitPlusSavings = benefitPlusSavings,
                        availableMargin = availableMargin,
                        variableExpensePercentage = variablePercentage
                    )
                }

                combine(
                    totalsFlow,
                    transactionRepository.getTransactionsByMonth(month, year)
                ) { summary, transactions ->
                    summary to transactions
                }.collect { (newSummary, transactions) ->
                    _summary.value = newSummary
                    _recentTransactions.value = transactions.take(10)
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun setMonth(month: Int, year: Int) {
        _currentMonth.value = month
        _currentYear.value = year
        loadDashboardData()
    }

    fun previousMonth() {
        if (_currentMonth.value == 1) {
            _currentMonth.value = 12
            _currentYear.value--
        } else {
            _currentMonth.value--
        }
        loadDashboardData()
    }

    fun nextMonth() {
        if (_currentMonth.value == 12) {
            _currentMonth.value = 1
            _currentYear.value++
        } else {
            _currentMonth.value++
        }
        loadDashboardData()
    }
}
