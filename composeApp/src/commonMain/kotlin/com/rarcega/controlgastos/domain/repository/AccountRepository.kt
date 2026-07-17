package com.rarcega.controlgastos.domain.repository

import com.rarcega.controlgastos.domain.model.Account
import com.rarcega.controlgastos.domain.model.AccountType
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAllActiveAccounts(): Flow<List<Account>>
    fun getAccountsByType(type: AccountType): Flow<List<Account>>
    suspend fun getAccountById(id: Long): Account?
    suspend fun insertAccount(account: Account): Long
    suspend fun updateAccount(account: Account)
    suspend fun deleteAccount(account: Account)
    suspend fun syncNordigenAccounts()
}
