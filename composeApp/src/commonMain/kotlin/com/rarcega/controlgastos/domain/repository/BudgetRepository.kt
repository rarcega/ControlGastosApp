package com.rarcega.controlgastos.domain.repository

import com.rarcega.controlgastos.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudgetsByMonth(month: Int, year: Int): Flow<List<Budget>>
    fun getBudgetByCategoryAndMonth(categoryId: Long, month: Int, year: Int): Flow<Budget?>
    suspend fun insertBudget(budget: Budget): Long
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)
}
