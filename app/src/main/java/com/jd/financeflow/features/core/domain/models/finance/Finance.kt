package com.jd.financeflow.features.core.domain.models.finance

import java.util.UUID

/**
 * Represents a financial transaction.
 *
 * @property id The unique identifier for the transaction.
 * @property title The title or description of the transaction.
 * @property description Additional details or notes about the transaction.
 * @property amount The monetary value of the transaction.
 * @property timeStampMs The timestamp indicating when the transaction started or has been made.
 * @property interval The frequency or interval at which the transaction occurs.
 * @property category The category or type of the transaction.
 * @property createdAt The timestamp indicating when the transaction was created or updated at last.
 */
data class Finance(
    val id: Int = UUID.randomUUID().hashCode(),
    val title: String,
    val description: String?,
    val amount: Double,
    val timeStampMs: Long,
    val interval: FinanceInterval,
    val category: FinanceCategory,
    val createdAt: Long = System.currentTimeMillis()
)
