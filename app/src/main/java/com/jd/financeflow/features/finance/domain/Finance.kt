package com.jd.financeflow.features.finance.domain

import java.util.UUID

/**
 * Represents a financial transaction.
 *
 * @property id The unique identifier for the transaction.
 * @property title The title or description of the transaction.
 * @property description Additional details or notes about the transaction.
 * @property amount The monetary value of the transaction.
 * @property interval The frequency or interval at which the transaction occurs.
 * @property type The type of the transaction (e.g., income, expense).
 * @property category The category or type of the transaction.
 */
data class Finance(
    val id: Int = UUID.randomUUID().hashCode(),
    val title: String,
    val description: String?,
    val amount: Double,
    val interval: FinanceInterval,
    val type: FinanceType,
    val category: FinanceCategory


)
