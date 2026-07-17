package com.rarcega.controlgastos.data.repository

import com.rarcega.controlgastos.data.local.TransactionDao
import com.rarcega.controlgastos.data.local.TransactionEntity
import com.rarcega.controlgastos.domain.model.Transaction
import com.rarcega.controlgastos.domain.model.TransactionType
import com.rarcega.controlgastos.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTransactionsByMonth(month: Int, year: Int): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByMonth(month, year).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTransactionsByTypeAndMonth(type: TransactionType, month: Int, year: Int): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByTypeAndMonth(type.name, month, year).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTotalByTypeAndMonth(type: TransactionType, month: Int, year: Int): Flow<Double> {
        return transactionDao.getTotalByTypeAndMonth(type.name, month, year).map { it ?: 0.0 }
    }

    override fun getTotalByCategoryAndMonth(categoryId: Long, month: Int, year: Int): Flow<Double> {
        return transactionDao.getTotalByCategoryAndMonth(categoryId, month, year).map { it ?: 0.0 }
    }

    override suspend fun getTransactionById(id: Long): Transaction? {
        return transactionDao.getTransactionById(id)?.toDomain()
    }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }

    private fun TransactionEntity.toDomain() = Transaction(
        id = id,
        description = description,
        amount = amount,
        type = TransactionType.valueOf(type),
        paymentMethod = com.rarcega.controlgastos.domain.model.PaymentMethod.valueOf(paymentMethod),
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

    private fun Transaction.toEntity() = TransactionEntity(
        id = id,
        description = description,
        amount = amount,
        type = type.name,
        paymentMethod = paymentMethod.name,
        categoryId = categoryId,
        categoryName = categoryName,
        date = date.toString(),
        month = month,
        year = year,
        accountId = accountId,
        accountName = accountName,
        notes = notes,
        createdAt = createdAt
    )
}
