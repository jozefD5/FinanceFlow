package com.jd.financeflow.features.financeList.presentation.financeFilter

import com.jd.financeflow.features.core.domain.models.finance.FinanceType
import com.jd.financeflow.features.core.domain.models.financeFilter.FilterConfiguration

sealed interface FinanceListFilterActions {
    data object OnClearError : FinanceListFilterActions
    data class OnApplyFilter(val callback: ((FilterConfiguration) -> Unit)? = null) :
        FinanceListFilterActions

    data object OnResetFilter : FinanceListFilterActions
    data object OnBackNavigationClicked : FinanceListFilterActions
    data class OnEnableCategories(val enable: Boolean) : FinanceListFilterActions
    data class OnUpdateType(val index: Int) : FinanceListFilterActions
    data class OnCategorySelected(val type: FinanceType, val index: Int) : FinanceListFilterActions
    data class OnEnableAmountRange(val enable: Boolean) : FinanceListFilterActions
    data class OnUpdateAmountRange(val mini: Float, val max: Float) : FinanceListFilterActions
}