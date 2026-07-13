package com.jd.financeflow.features.finance.presentation.financeDetails

import androidx.compose.runtime.Stable

import com.jd.financeflow.features.core.presentation.UiText

@Stable
data class FinanceDetailsState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val currencyCode: String = "EUR",

    val invalidTitle: Boolean = false,
    val invalidAmount: Boolean = false,

    val title: String = "",
    val description: String = "",
    val amount: String = "",
    val startDate: Long = System.currentTimeMillis(),
    val financeIntervalIndex: Int = 0,
    val financeTypeIndex: Int = 0,
    val financeCategoryIndex: Int = 0,
)