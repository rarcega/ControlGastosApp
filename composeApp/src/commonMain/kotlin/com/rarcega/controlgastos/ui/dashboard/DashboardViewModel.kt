package com.rarcega.controlgastos.ui.dashboard

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rarcega.controlgastos.data.local.MonthlyConfigDao
import com.rarcega.controlgastos.data.local.MonthlyConfigEntity
import com.rarcega.controlgastos.data.local.TransactionDao
import com.rarcega.controlgastos.data.local.TransactionEntity
import com.rarcega.controlgastos.domain.model.MonthlySummary
import com.rarcega.controlgastos.domain.model.PaymentMethod
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
    private val budgetRepository: BudgetRepository,
    private val monthlyConfigDao: MonthlyConfigDao
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

    private val _fixedExpenses = MutableStateFlow<List<Transaction>>(emptyList())
    val fixedExpenses: StateFlow<List<Transaction>> = _fixedExpenses

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _monthlyConfig = MutableStateFlow(MonthlyConfigEntity(
        month = _currentMonth.value,
        year = _currentYear.value
    ))
    val monthlyConfig: StateFlow<MonthlyConfigEntity> = _monthlyConfig

    companion object {
        val defaultFixedExpenses = listOf(
            "Alquiler / Hipoteca" to 0.0,
            "Luz" to 0.0,
            "Agua" to 0.0,
            "Gas" to 0.0,
            "Internet" to 0.0,
            "Seguro hogar" to 0.0,
            "Seguro coche" to 0.0,
            "Cuota gimnasio" to 0.0,
            "Suscripciones" to 0.0
        )
    }

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val month = _currentMonth.value
                val year = _currentYear.value

                // Load or create monthly config
                val config = monthlyConfigDao.getConfigOnce(month, year)
                if (config == null) {
                    val newConfig = MonthlyConfigEntity(month = month, year = year)
                    monthlyConfigDao.upsertConfig(newConfig)
                    _monthlyConfig.value = newConfig
                    createDefaultFixedExpenses(month, year)
                } else {
                    _monthlyConfig.value = config
                }

                val currentConfig = _monthlyConfig.value

                val totalsFlow = combine(
                    transactionRepository.getTotalByTypeAndMonth(TransactionType.INCOME, month, year),
                    transactionRepository.getTotalByTypeAndMonth(TransactionType.SAVING, month, year),
                    transactionRepository.getTotalByTypeAndMonth(TransactionType.EXPENSE_FIXED, month, year),
                    transactionRepository.getTotalByTypeAndMonth(TransactionType.EXPENSE_VARIABLE, month, year),
                    transactionRepository.getTotalByTypeAndMonth(TransactionType.EXPENSE_CASH, month, year)
                ) { income, savings, fixedExpenses, variableExpenses, cashExpenses ->
                    val totalSpent = fixedExpenses + variableExpenses + cashExpenses
                    val initialBalance = currentConfig.initialBalance
                    val expenseLimit = currentConfig.expenseLimit
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
                    _recentTransactions.value = transactions
                        .filter { it.type != TransactionType.EXPENSE_FIXED }
                        .take(10)
                    _fixedExpenses.value = transactions
                        .filter { it.type == TransactionType.EXPENSE_FIXED }
                        .sortedBy { it.description }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    private suspend fun createDefaultFixedExpenses(month: Int, year: Int) {
        val today = kotlinx.datetime.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        defaultFixedExpenses.forEach { (description, amount) ->
            val entity = TransactionEntity(
                description = description,
                amount = amount,
                type = TransactionType.EXPENSE_FIXED.name,
                paymentMethod = PaymentMethod.TRANSFER.name,
                categoryId = 0,
                categoryName = "Gastos Fijos",
                date = today.toString(),
                month = month,
                year = year,
                createdAt = System.currentTimeMillis()
            )
            transactionRepository.insertTransaction(entity.toDomain())
        }
    }

    fun updateInitialBalance(amount: Double) {
        viewModelScope.launch {
            val month = _currentMonth.value
            val year = _currentYear.value
            val updated = _monthlyConfig.value.copy(initialBalance = amount)
            monthlyConfigDao.upsertConfig(updated)
            _monthlyConfig.value = updated
            loadDashboardData()
        }
    }

    fun updateExpenseLimit(amount: Double) {
        viewModelScope.launch {
            val month = _currentMonth.value
            val year = _currentYear.value
            val updated = _monthlyConfig.value.copy(expenseLimit = amount)
            monthlyConfigDao.upsertConfig(updated)
            _monthlyConfig.value = updated
            loadDashboardData()
        }
    }

    fun updateFixedExpenseAmount(transaction: Transaction, newAmount: Double) {
        viewModelScope.launch {
            val updated = transaction.copy(amount = newAmount)
            transactionRepository.updateTransaction(updated)
            loadDashboardData()
        }
    }

    fun updateFixedExpenseDescription(transaction: Transaction, newDescription: String) {
        viewModelScope.launch {
            val updated = transaction.copy(description = newDescription)
            transactionRepository.updateTransaction(updated)
            loadDashboardData()
        }
    }

    fun addFixedExpense(description: String, amount: Double) {
        viewModelScope.launch {
            val month = _currentMonth.value
            val year = _currentYear.value
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val entity = Transaction(
                description = description,
                amount = amount,
                type = TransactionType.EXPENSE_FIXED,
                paymentMethod = PaymentMethod.TRANSFER,
                categoryId = 0,
                categoryName = "Gastos Fijos",
                date = today,
                month = month,
                year = year,
                createdAt = System.currentTimeMillis()
            )
            transactionRepository.insertTransaction(entity)
            loadDashboardData()
        }
    }

    fun deleteFixedExpense(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
            loadDashboardData()
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

    private fun TransactionEntity.toDomain() = Transaction(
        id = id,
        description = description,
        amount = amount,
        type = TransactionType.valueOf(type),
        paymentMethod = PaymentMethod.valueOf(paymentMethod),
        categoryId = categoryId,
        categoryName = categoryName,
        date = kotlinx.datetime.LocalDate.parse(date),
        month = month,
        year = year,
        accountId = accountId,
        accountName = accountName,
        notes = notes,
        createdAt = createdAt
    )
}
