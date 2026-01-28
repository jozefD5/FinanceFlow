package com.jd.financeflow.features.finance.presentation.financeListFilter

import com.jd.financeflow.features.finance.domain.models.FinanceType

sealed interface FinanceListFilterActions {
    data object OnClearError : FinanceListFilterActions
    data object OnApplyFilter : FinanceListFilterActions
    data class OnUpdateSort(val index: Int) : FinanceListFilterActions
    data class OnUpdateType(val index: Int) : FinanceListFilterActions
    data class OnEnableCategories(val enable: Boolean) : FinanceListFilterActions
    data class OnCategorySelected(val type: FinanceType, val index: Int) : FinanceListFilterActions
    data class OnEnableAmountRange(val enable: Boolean) : FinanceListFilterActions
    data class OnUpdateAmountRange(val mini: Float, val max: Float) : FinanceListFilterActions
}