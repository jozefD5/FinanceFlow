package com.jd.financeflow.features.finance.presentation.financeList

import com.jd.financeflow.features.core.domain.finance.Finance

/**
 * Actions that can be performed by the FinanceList screen.
 */
sealed interface FinanceListActions {
    data object OnClearError : FinanceListActions
    data object OnAddFinanceClicked : FinanceListActions
    data object OnFilterClicked : FinanceListActions
    data class OnFinanceItemClicked(val id: Int) : FinanceListActions
    data class OnFinanceItemDelete(val finance: Finance) : FinanceListActions
    data class OnSelectedDeleteFinanceItem(
        val showDialog: Boolean,
        val finance: Finance?
    ) : FinanceListActions

    data class OnEnableSearch(val value: Boolean) : FinanceListActions
    data class OnUpdateSearchQuery(val value: String) : FinanceListActions
    data object OnRefresh : FinanceListActions
}