package com.rarcega.controlgastos.data.repository

import com.rarcega.controlgastos.data.local.AccountDao
import com.rarcega.controlgastos.data.local.AccountEntity
import com.rarcega.controlgastos.data.remote.NordigenApi
import com.rarcega.controlgastos.domain.model.Account
import com.rarcega.controlgastos.domain.model.AccountType
import com.rarcega.controlgastos.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountRepositoryImpl(
    private val accountDao: AccountDao,
    private val nordigenApi: NordigenApi
) : AccountRepository {

    override fun getAllActiveAccounts(): Flow<List<Account>> {
        return accountDao.getAllActiveAccounts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAccountsByType(type: AccountType): Flow<List<Account>> {
        return accountDao.getAccountsByType(type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAccountById(id: Long): Account? {
        return accountDao.getAccountById(id)?.toDomain()
    }

    override suspend fun insertAccount(account: Account): Long {
        return accountDao.insertAccount(account.toEntity())
    }

    override suspend fun updateAccount(account: Account) {
        accountDao.updateAccount(account.toEntity())
    }

    override suspend fun deleteAccount(account: Account) {
        accountDao.deleteAccount(account.toEntity())
    }

    override suspend fun syncNordigenAccounts() {
        try {
            val nordigenAccounts = nordigenApi.getAccounts()
            for (nordigenAccount in nordigenAccounts) {
                val existingAccount = accountDao.getAccountByNordigenId(nordigenAccount.id)
                val balances = nordigenApi.getBalances(nordigenAccount.id)
                val balance = balances.firstOrNull()?.balanceAmount?.value?.toDoubleOrNull() ?: 0.0

                if (existingAccount != null) {
                    accountDao.updateBalance(existingAccount.id, balance)
                } else {
                    val newAccount = AccountEntity(
                        name = nordigenAccount.name ?: "Cuenta Nordigen",
                        type = AccountType.BANK.name,
                        bank = nordigenAccount.institutionId ?: "",
                        balance = balance,
                        nordigenAccountId = nordigenAccount.id,
                        nordigenInstitutionId = nordigenAccount.institutionId
                    )
                    accountDao.insertAccount(newAccount)
                }
            }
        } catch (e: Exception) {
            // Handle error silently for now
        }
    }

    private fun AccountEntity.toDomain() = Account(
        id = id,
        name = name,
        type = AccountType.valueOf(type),
        bank = bank,
        balance = balance,
        nordigenAccountId = nordigenAccountId,
        nordigenInstitutionId = nordigenInstitutionId,
        iban = iban,
        currency = currency,
        isActive = isActive
    )

    private fun Account.toEntity() = AccountEntity(
        id = id,
        name = name,
        type = type.name,
        bank = bank,
        balance = balance,
        nordigenAccountId = nordigenAccountId,
        nordigenInstitutionId = nordigenInstitutionId,
        iban = iban,
        currency = currency,
        isActive = isActive
    )
}
