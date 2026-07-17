package com.rarcega.controlgastos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val bank: String = "",
    val balance: Double = 0.0,
    val nordigenAccountId: String? = null,
    val nordigenInstitutionId: String? = null,
    val iban: String = "",
    val currency: String = "EUR",
    val isActive: Boolean = true
)
