package com.rarcega.controlgastos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val amount: Double,
    val type: String,
    val paymentMethod: String,
    val categoryId: Long,
    val categoryName: String = "",
    val date: String,
    val month: Int,
    val year: Int,
    val accountId: Long? = null,
    val accountName: String = "",
    val notes: String = "",
    val createdAt: Long = 0L
)
