package com.jd.financeflow.features.finance.presentation.financeList

import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceType

/**
 * Represents actions that can be performed on the FinanceList screen.
 */
sealed interface FinanceListActions {
    class ClearError : FinanceListActions
    data class FinanceClicked(val id: Int) : FinanceListActions
    data class UpdateSearchQuery(val value: String) : FinanceListActions
    class OnSearch : FinanceListActions
    data class UpdateFinanceTypeFilter(val value: FinanceType?) : FinanceListActions
    data class UpdateFinanceCategoryFilter(val value: FinanceCategory?) : FinanceListActions
}