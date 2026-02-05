package com.jd.financeflow.features.finance.presentation.financeList

import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.finance.domain.models.FilterConfiguration
import com.jd.financeflow.features.finance.domain.models.Finance

/**
 * Represents the state of the FinanceList screen.
 */
data class FinanceListState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val currencyCode: String = "EUR",
    val filterConfiguration: FilterConfiguration? = null,

    val showDeleteDialog: Boolean = false,
    val selectedDeleteFinance: Finance? = null,

    val searchQuery: String = "",
    val finances: List<Finance> = emptyList(),
)
