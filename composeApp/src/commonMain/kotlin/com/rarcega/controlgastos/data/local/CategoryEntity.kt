package com.rarcega.controlgastos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String = "",
    val color: Long = 0xFF6B7280,
    val isDefault: Boolean = false
)
