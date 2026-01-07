package com.jd.financeflow.features.finance.presentation.financeList

import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceType

/**
 * Represents the state of the FinanceList screen.
 */
data class FinanceListState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val currencyCode: String = "EUR",

    val searchQuery: String = "",

    val type: FinanceType? = null,
    val category: FinanceCategory? = null,
    val finances: List<Finance> = emptyList(),
)
