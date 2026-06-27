package com.jd.financeflow.features.finance.presentation.charts.financeChartCard

import com.jd.financeflow.features.core.presentation.UiText

/**
 * Finance charts view state
 */
data class FinanceChartCardState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val currencyCode: String = "EUR",

    val income: List<Double> = emptyList(),
    val expense: List<Double> = emptyList(),
    val balance: List<Double> = emptyList(),
)
