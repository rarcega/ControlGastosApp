package com.rarcega.controlgastos.domain.repository

import com.rarcega.controlgastos.domain.model.Transaction
import com.rarcega.controlgastos.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsByMonth(month: Int, year: Int): Flow<List<Transaction>>
    fun getTransactionsByTypeAndMonth(type: TransactionType, month: Int, year: Int): Flow<List<Transaction>>
    fun getTotalByTypeAndMonth(type: TransactionType, month: Int, year: Int): Flow<Double>
    fun getTotalByCategoryAndMonth(categoryId: Long, month: Int, year: Int): Flow<Double>
    suspend fun getTransactionById(id: Long): Transaction?
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
}
