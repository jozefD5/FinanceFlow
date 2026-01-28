package com.jd.financeflow.features.finance.domain.models

/**
 * Represents the configuration for filtering finance transactions.
 */
data class FilterConfiguration(
    val sortOrder: SortOrder = SortOrder.NEWEST_FIRST,
    val type: FinanceType? = null,
    val categories: List<FinanceCategory>? = null,
    val miniAmount: Double? = null,
    val maxAmount: Double? = null
)
