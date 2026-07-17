package com.rarcega.controlgastos.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FinancialPosition(
    val bankAccounts: List<Account> = emptyList(),
    val otherAssets: List<Account> = emptyList(),
    val pensionPlan: Account? = null,
    val totalBankBalance: Double = 0.0,
    val totalOtherAssets: Double = 0.0,
    val totalPension: Double = 0.0,
    val realMoney: Double = 0.0,
    val debt: Double = 0.0,
    val balance: Double = 0.0,
    val patrimony: Double = 0.0
)
