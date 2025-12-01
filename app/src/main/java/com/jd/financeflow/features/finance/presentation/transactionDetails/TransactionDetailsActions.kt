package com.jd.financeflow.features.finance.presentation.transactionDetails

/**
 * Represents actions that can be performed on the transaction details screen.
 */
sealed interface TransactionDetailsActions {
    object ClearError : TransactionDetailsActions
    object SaveTransaction : TransactionDetailsActions
    data class UpdateTitle(val title: String) : TransactionDetailsActions
    data class UpdateDescription(val description: String) : TransactionDetailsActions
    data class UpdateAmount(val amount: String) : TransactionDetailsActions
    data class UpdateFinanceInterval(val financeIntervalIndex: Int) : TransactionDetailsActions
    data class UpdateFinanceType(val financeTypeIndex: Int) : TransactionDetailsActions
    data class UpdateFinanceCategory(val financeCategoryIndex: Int) : TransactionDetailsActions
}