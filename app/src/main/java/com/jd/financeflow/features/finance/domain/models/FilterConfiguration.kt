package com.jd.financeflow.features.finance.domain.models

/**
 * Represents the configuration for filtering finance transactions.
 */
data class FilterConfiguration(
    // Sort order for the transactions
    val sortOrder: SortOrder = SortOrder.NEWEST_FIRST,

    // Type of the transactions to be filtered
    val type: FinanceType? = null,

    // Categories of the transactions to be filtered
    val categories: List<FinanceCategory>? = null,

    // Amount limits for the transactions to be filtered
    val miniSetAmount: Double? = null,
    val maxSetAmount: Double? = null,

    // Amount lower and upper limits for UI state
    val miniAmountLimit: Double? = null,
    val maxAmountLimit: Double? = null,
)
