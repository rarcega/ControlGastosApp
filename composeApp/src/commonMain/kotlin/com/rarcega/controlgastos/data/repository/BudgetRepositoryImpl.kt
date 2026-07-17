package com.rarcega.controlgastos.data.repository

import com.rarcega.controlgastos.data.local.BudgetDao
import com.rarcega.controlgastos.data.local.BudgetEntity
import com.rarcega.controlgastos.domain.model.Budget
import com.rarcega.controlgastos.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetRepositoryImpl(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override fun getBudgetsByMonth(month: Int, year: Int): Flow<List<Budget>> {
        return budgetDao.getBudgetsByMonth(month, year).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getBudgetByCategoryAndMonth(categoryId: Long, month: Int, year: Int): Flow<Budget?> {
        return budgetDao.getBudgetByCategoryAndMonth(categoryId, month, year).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun insertBudget(budget: Budget): Long {
        return budgetDao.insertBudget(budget.toEntity())
    }

    override suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget.toEntity())
    }

    override suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget.toEntity())
    }

    private fun BudgetEntity.toDomain() = Budget(
        id = id,
        month = month,
        year = year,
        categoryId = categoryId,
        categoryName = categoryName,
        limitAmount = limitAmount,
        spentAmount = spentAmount
    )

    private fun Budget.toEntity() = BudgetEntity(
        id = id,
        month = month,
        year = year,
        categoryId = categoryId,
        categoryName = categoryName,
        limitAmount = limitAmount,
        spentAmount = spentAmount
    )
}
