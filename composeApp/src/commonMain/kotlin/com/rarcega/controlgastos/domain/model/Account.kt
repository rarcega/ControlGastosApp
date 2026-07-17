package com.rarcega.controlgastos.domain.model

import kotlinx.serialization.Serializable

enum class AccountType {
    BANK,
    SAVINGS,
    CASH,
    INVESTMENT,
    PENSION
}

@Serializable
data class Account(
    val id: Long = 0,
    val name: String,
    val type: AccountType,
    val bank: String = "",
    val balance: Double = 0.0,
    val nordigenAccountId: String? = null,
    val nordigenInstitutionId: String? = null,
    val iban: String = "",
    val currency: String = "EUR",
    val isActive: Boolean = true
)
