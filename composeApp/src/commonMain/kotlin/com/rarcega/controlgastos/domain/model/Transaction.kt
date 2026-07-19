package com.rarcega.controlgastos.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

enum class TransactionType(val displayName: String) {
    INCOME("Ingreso"),
    EXPENSE_VARIABLE("Gasto Variable"),
    EXPENSE_CASH("Gasto Efectivo"),
    EXPENSE_FIXED("Gasto Fijo"),
    SAVING("Ahorro")
}

enum class PaymentMethod(val displayName: String) {
    CARD("Tarjeta"),
    CASH("Efectivo"),
    TRANSFER("Transferencia")
}

@Serializable
data class Transaction(
    val id: Long = 0,
    val description: String,
    val amount: Double,
    val type: TransactionType,
    val paymentMethod: PaymentMethod,
    val categoryId: Long,
    val categoryName: String = "",
    val date: LocalDate,
    val month: Int,
    val year: Int,
    val accountId: Long? = null,
    val accountName: String = "",
    val notes: String = "",
    val createdAt: Long = 0L
)
