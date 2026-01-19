package com.jd.financeflow.features.finance.presentation.financeListFilter

import com.jd.financeflow.features.finance.domain.models.FinanceCategory

sealed interface FinanceListFilterActions {
    data object OnClearError : FinanceListFilterActions
    data class OnUpdateType(val typeIndex: Int) : FinanceListFilterActions
    data class OnEnableCategories(val enable: Boolean) : FinanceListFilterActions
    data class OnAddCategory(val category: FinanceCategory)
    data class OnRemoveCategory(val category: FinanceCategory)
    data class OnEnableAmountRange(val enable: Boolean) : FinanceListFilterActions
    data class OnUpdateAmountRange(val mini: Double?, val max: Double?) : FinanceListFilterActions
}