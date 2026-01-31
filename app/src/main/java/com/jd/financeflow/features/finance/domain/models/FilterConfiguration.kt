package com.jd.financeflow.features.finance.domain.models

import kotlinx.serialization.Serializable

/**
 * Represents the configuration for filtering finance transactions.
 */
@Serializable
data class FilterConfiguration(
    // Sort order for the transactions
    val sortOrder: SortOrder = SortOrder.NEWEST_FIRST,

    // Type of the transactions to be filtered
    val type: FinanceType? = null,

    // Indexes of the categories to be filtered
    val selectedIncomeIndexes: List<Int> = emptyList(),
    val selectedExpenseIndexes: List<Int> = emptyList(),

    // Amount limits for the transactions to be filtered
    val miniSetAmount: Double? = null,
    val maxSetAmount: Double? = null,

    // Amount lower and upper limits for UI state
    val miniAmountLimit: Double? = null,
    val maxAmountLimit: Double? = null,
)
