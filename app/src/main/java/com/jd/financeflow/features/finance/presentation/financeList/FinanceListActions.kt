package com.jd.financeflow.features.finance.presentation.financeList

import com.jd.financeflow.features.finance.domain.models.FilterConfiguration
import com.jd.financeflow.features.finance.domain.models.Finance

/**
 * Represents actions that can be performed on the FinanceList screen.
 */
sealed interface FinanceListActions {
    data object OnClearError : FinanceListActions
    data object OnAddFinanceClicked : FinanceListActions
    data object OnFilterClicked : FinanceListActions
    data class OnFinanceItemClicked(val id: Int) : FinanceListActions
    data class OnFinanceItemDelete(val finance: Finance) : FinanceListActions
    data class OnSelectedDeleteFinanceItem(
        val shoeDialog: Boolean,
        val finance: Finance?
    ) : FinanceListActions

    data class OnEnableSearch(val value: Boolean) : FinanceListActions
    data class OnUpdateSearchQuery(val value: String) : FinanceListActions
    data object OnSearch : FinanceListActions
    data object OnRefresh : FinanceListActions
}