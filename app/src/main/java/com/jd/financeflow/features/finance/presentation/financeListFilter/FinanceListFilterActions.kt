package com.jd.financeflow.features.finance.presentation.financeListFilter

sealed interface FinanceListFilterActions {
    data object OnClearError : FinanceListFilterActions
    data object OnApplyFilter : FinanceListFilterActions
    data class OnUpdateType(val typeIndex: Int) : FinanceListFilterActions
    data class OnEnableCategories(val enable: Boolean) : FinanceListFilterActions
    data class OnEnableAmountRange(val enable: Boolean) : FinanceListFilterActions
    data class OnUpdateAmountRange(val mini: Double?, val max: Double?) : FinanceListFilterActions
}