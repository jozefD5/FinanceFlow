package com.jd.financeflow.features.finance.domain.models

import kotlinx.serialization.Serializable

/**
 * Represents the configuration for filtering finance transactions.
 */
@Serializable
data class FilterConfiguration(
    // Sort order for the transactions
    val sortOrder: SortOrder = SortOrder.NEWEST_FIRST,

    // Selected income and expense categories. Set both fields to null if no
    // categories are selected. Position of each element corresponds to category position of each
    // enum for the type of category (income or expense)
    val enableCategoryFilter: Boolean = false,
    val selectedIncomeIndexes: List<Boolean>? = null,
    val selectedExpenseIndexes: List<Boolean>? = null,

    // Type of the transactions to be filtered
    val type: FinanceType? = null,

    // Amount limits for the transactions to be filtered
    val enableAmountFilter: Boolean = false,
    val miniSetAmount: Double? = null,
    val maxSetAmount: Double? = null,

    // Amount lower and upper limits for UI state
    val miniAmountLimit: Double? = null,
    val maxAmountLimit: Double? = null,
)
