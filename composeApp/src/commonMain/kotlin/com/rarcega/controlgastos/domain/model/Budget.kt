package com.rarcega.controlgastos.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Budget(
    val id: Long = 0,
    val month: Int,
    val year: Int,
    val categoryId: Long,
    val categoryName: String = "",
    val limitAmount: Double,
    val spentAmount: Double = 0.0
) {
    val remainingAmount: Double get() = limitAmount - spentAmount
    val percentageUsed: Double get() = if (limitAmount > 0) (spentAmount / limitAmount) * 100 else 0.0
    val isOverBudget: Boolean get() = spentAmount > limitAmount
}
