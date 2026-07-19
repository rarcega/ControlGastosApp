package com.rarcega.controlgastos.data.local

import androidx.room.Entity

@Entity(
    tableName = "monthly_config",
    primaryKeys = ["month", "year"]
)
data class MonthlyConfigEntity(
    val month: Int,
    val year: Int,
    val initialBalance: Double = 0.0,
    val expenseLimit: Double = 1500.0
)
