package com.jd.financeflow.features.finance.presentation.addTransaction

/**
 * Represents the state of the add transaction screen.
 */
data class AddTransactionState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val title: String = "",
    val description: String = "",
    val amount: String = "",
    val financeIntervalIndex: Int = 0,
    val financeTypeIndex: Int = 0,
    val financeCategoryIndex: Int = 0,
)