package com.jd.financeflow.features.finance.data.localDataSource

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceInterval

/**
 * Represents a financial transaction entity in the database.
 *
 * @property id The unique identifier for the transaction.
 * @property title The title or description of the transaction.
 * @property description Additional details or notes about the transaction.
 * @property amount The monetary value of the transaction.
 * @property interval The frequency or interval at which the transaction occurs.
 * @property category The category or type of the transaction
 * @property createdAt The timestamp indicating when the transaction was created or updated at last.
 */
@Entity
data class FinanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val description: String?,
    val amount: Double,
    val interval: FinanceInterval,
    val category: FinanceCategory,
    val createdAt: Long = System.currentTimeMillis()
)