package com.jd.financeflow.features.finance.presentation.financeDetails

/**
 * Represents actions that can be performed on the finance transaction details screen.
 */
sealed interface FinanceDetailsActions {
    class ClearError : FinanceDetailsActions
    data class OnSaveFinance(val action: (() -> Unit)? = null) : FinanceDetailsActions
    object OnBackNavigationClicked : FinanceDetailsActions
    data class OnUpdateTitle(val title: String) : FinanceDetailsActions
    data class OnUpdateDescription(val description: String) : FinanceDetailsActions
    data class OnUpdateAmount(val amount: String) : FinanceDetailsActions
    data class OnUpdateStartDate(val startDate: Long) : FinanceDetailsActions
    data class OnUpdateFinanceInterval(val financeIntervalIndex: Int) : FinanceDetailsActions
    data class OnUpdateFinanceType(val financeTypeIndex: Int) : FinanceDetailsActions
    data class OnUpdateFinanceCategory(val financeCategoryIndex: Int) : FinanceDetailsActions
}