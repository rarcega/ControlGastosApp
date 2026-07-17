package com.rarcega.controlgastos.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rarcega.controlgastos.domain.model.Account
import com.rarcega.controlgastos.domain.model.AccountType
import com.rarcega.controlgastos.domain.model.FinancialPosition
import com.rarcega.controlgastos.domain.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountsViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts

    private val _financialPosition = MutableStateFlow(FinancialPosition())
    val financialPosition: StateFlow<FinancialPosition> = _financialPosition

    init {
        loadAccounts()
    }

    fun loadAccounts() {
        viewModelScope.launch {
            accountRepository.getAllActiveAccounts().collect { accounts ->
                _accounts.value = accounts
                calculateFinancialPosition(accounts)
            }
        }
    }

    private fun calculateFinancialPosition(accounts: List<Account>) {
        val bankAccounts = accounts.filter { it.type == AccountType.BANK }
        val otherAssets = accounts.filter { it.type != AccountType.BANK && it.type != AccountType.PENSION }
        val pension = accounts.find { it.type == AccountType.PENSION }

        val totalBankBalance = bankAccounts.sumOf { it.balance }
        val totalOtherAssets = otherAssets.sumOf { it.balance }
        val totalPension = pension?.balance ?: 0.0
        val realMoney = totalBankBalance + totalOtherAssets
        val debt = 0.0 // TODO: Get from data
        val balance = realMoney - debt
        val patrimony = balance + totalPension

        _financialPosition.value = FinancialPosition(
            bankAccounts = bankAccounts,
            otherAssets = otherAssets,
            pensionPlan = pension,
            totalBankBalance = totalBankBalance,
            totalOtherAssets = totalOtherAssets,
            totalPension = totalPension,
            realMoney = realMoney,
            debt = debt,
            balance = balance,
            patrimony = patrimony
        )
    }

    fun syncNordigenAccounts() {
        viewModelScope.launch {
            accountRepository.syncNordigenAccounts()
            loadAccounts()
        }
    }

    fun addAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.insertAccount(account)
            loadAccounts()
        }
    }

    fun updateAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.updateAccount(account)
            loadAccounts()
        }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.deleteAccount(account)
            loadAccounts()
        }
    }
}
