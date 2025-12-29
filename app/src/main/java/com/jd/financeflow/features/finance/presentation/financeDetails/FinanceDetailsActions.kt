package com.jd.financeflow.features.finance.presentation.financeDetails

/**
 * Represents actions that can be performed on the finance transaction details screen.
 */
sealed interface FinanceDetailsActions {
    class ClearError : FinanceDetailsActions
    data class SaveTransaction(val action: (() -> Unit)? = null) : FinanceDetailsActions
    data class UpdateTitle(val title: String) : FinanceDetailsActions
    data class UpdateDescription(val description: String) : FinanceDetailsActions
    data class UpdateAmount(val amount: String) : FinanceDetailsActions
    data class UpdateFinanceInterval(val financeIntervalIndex: Int) : FinanceDetailsActions
    data class UpdateFinanceType(val financeTypeIndex: Int) : FinanceDetailsActions
    data class UpdateFinanceCategory(val financeCategoryIndex: Int) : FinanceDetailsActions
}