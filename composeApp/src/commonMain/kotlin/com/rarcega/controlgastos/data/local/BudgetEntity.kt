package com.rarcega.controlgastos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val month: Int,
    val year: Int,
    val categoryId: Long,
    val categoryName: String = "",
    val limitAmount: Double,
    val spentAmount: Double = 0.0
)
