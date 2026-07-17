package com.rarcega.controlgastos.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String = "",
    val color: Long = 0xFF6B7280,
    val isDefault: Boolean = false
)

enum class DefaultCategory(val displayName: String, val icon: String) {
    ALIMENTACION("Alimentación", "🛒"),
    BARES("Bares", "🍺"),
    COCHE("Coche", "🚗"),
    GATA("Gata", "🐱"),
    GYM("Gym", "💪"),
    VIAJES("Viajes", "✈️"),
    CUMPLE("Cumple", "🎂"),
    TRANSPORTE("Transporte", "🚌"),
    COFRADIA("Cofradía", "⛪"),
    FARMACIA("Farmacia", "💊"),
    LIBROS("Libros", "📚"),
    IMPUESTOS("Impuestos", "🏛️"),
    OTROS("Otros", "📦")
}
