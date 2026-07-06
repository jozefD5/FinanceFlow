package com.jd.financeflow.features.finance.presentation.financeList

import androidx.compose.runtime.Stable

import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.finance.domain.models.FilterConfiguration
import com.jd.financeflow.features.finance.domain.models.Finance

@Stable
data class FinanceListState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val currencyCode: String = "EUR",

    val currentBalance: Double = 0.0,
    val filterConfiguration: FilterConfiguration? = null,

    val showDeleteDialog: Boolean = false,
    val selectedDeleteFinance: Finance? = null,

    val isSearching: Boolean = false,
    val searchQuery: String = "",
    val finances: List<Finance> = emptyList()
)
