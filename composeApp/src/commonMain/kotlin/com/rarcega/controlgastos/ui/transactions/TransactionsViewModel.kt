package com.rarcega.controlgastos.ui.transactions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rarcega.controlgastos.domain.model.Category
import com.rarcega.controlgastos.domain.model.Transaction
import com.rarcega.controlgastos.domain.model.TransactionType
import com.rarcega.controlgastos.domain.model.PaymentMethod
import com.rarcega.controlgastos.domain.repository.CategoryRepository
import com.rarcega.controlgastos.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TransactionsViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _selectedFilter = MutableStateFlow<TransactionType?>(null)
    val selectedFilter: StateFlow<TransactionType?> = _selectedFilter

    private val _currentMonth = mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).monthNumber)
    private val _currentYear = mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year)

    init {
        loadTransactions()
        loadCategories()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            val filter = _selectedFilter.value
            if (filter != null) {
                transactionRepository.getTransactionsByTypeAndMonth(filter, _currentMonth.value, _currentYear.value)
                    .collect { _transactions.value = it }
            } else {
                transactionRepository.getTransactionsByMonth(_currentMonth.value, _currentYear.value)
                    .collect { _transactions.value = it }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { _categories.value = it }
        }
    }

    fun setFilter(type: TransactionType?) {
        _selectedFilter.value = type
        loadTransactions()
    }

    fun addTransaction(
        description: String,
        amount: Double,
        type: TransactionType,
        paymentMethod: PaymentMethod,
        categoryId: Long,
        categoryName: String,
        date: kotlinx.datetime.LocalDate,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                description = description,
                amount = amount,
                type = type,
                paymentMethod = paymentMethod,
                categoryId = categoryId,
                categoryName = categoryName,
                date = date,
                month = date.monthNumber,
                year = date.year,
                notes = notes,
                createdAt = System.currentTimeMillis()
            )
            transactionRepository.insertTransaction(transaction)
            loadTransactions()
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
            loadTransactions()
        }
    }
}
