package com.rarcega.controlgastos.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MonthlySummary(
    val month: Int,
    val year: Int,
    val initialBalance: Double = 0.0,
    val income: Double = 0.0,
    val savings: Double = 0.0,
    val expenseLimit: Double = 0.0,
    val fixedExpenses: Double = 0.0,
    val variableExpenses: Double = 0.0,
    val cashExpenses: Double = 0.0,
    val totalSpent: Double = 0.0,
    val finalBalance: Double = 0.0,
    val benefit: Double = 0.0,
    val benefitPlusSavings: Double = 0.0,
    val availableMargin: Double = 0.0,
    val variableExpensePercentage: Double = 0.0
)
