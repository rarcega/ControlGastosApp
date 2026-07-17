package com.rarcega.controlgastos.data.repository

import com.rarcega.controlgastos.data.local.CategoryDao
import com.rarcega.controlgastos.data.local.CategoryEntity
import com.rarcega.controlgastos.domain.model.Category
import com.rarcega.controlgastos.domain.model.DefaultCategory
import com.rarcega.controlgastos.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)?.toDomain()
    }

    override suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category.toEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category.toEntity())
    }

    override suspend fun initializeDefaultCategories() {
        if (categoryDao.getCategoryCount() == 0) {
            val defaultCategories = DefaultCategory.entries.map { default ->
                CategoryEntity(
                    name = default.displayName,
                    icon = default.icon,
                    isDefault = true
                )
            }
            categoryDao.insertCategories(defaultCategories)
        }
    }

    private fun CategoryEntity.toDomain() = Category(
        id = id,
        name = name,
        icon = icon,
        color = color,
        isDefault = isDefault
    )

    private fun Category.toEntity() = CategoryEntity(
        id = id,
        name = name,
        icon = icon,
        color = color,
        isDefault = isDefault
    )
}
