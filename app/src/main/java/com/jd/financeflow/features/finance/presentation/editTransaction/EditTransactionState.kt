package com.jd.financeflow.features.finance.presentation.editTransaction

import com.jd.financeflow.features.core.presentation.UiText

/**
 * Represents the state of the add transaction screen.
 */
data class EditTransactionState(
    val isLoading: Boolean = false,
    val error: UiText? = null,

    val invalidTitle: Boolean = false,
    val invalidAmount: Boolean = false,

    val title: String = "",
    val description: String = "",
    val amount: String = "",
    val financeIntervalIndex: Int = 0,
    val financeTypeIndex: Int = 0,
    val financeCategoryIndex: Int = 0,
)