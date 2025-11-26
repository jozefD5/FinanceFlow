package com.jd.financeflow.features.finance.presentation.editTransaction

/**
 * Represents actions that can be performed on the edit transaction screen.
 */
sealed interface EditTransactionActions {
    object ClearError : EditTransactionActions
    object SaveTransaction : EditTransactionActions
    data class UpdateTitle(val title: String) : EditTransactionActions
    data class UpdateDescription(val description: String) : EditTransactionActions
    data class UpdateAmount(val amount: String) : EditTransactionActions
    data class UpdateFinanceInterval(val financeIntervalIndex: Int) : EditTransactionActions
    data class UpdateFinanceType(val financeTypeIndex: Int) : EditTransactionActions
    data class UpdateFinanceCategory(val financeCategoryIndex: Int) : EditTransactionActions
}