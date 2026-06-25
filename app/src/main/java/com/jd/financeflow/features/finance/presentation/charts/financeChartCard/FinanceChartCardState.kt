package com.jd.financeflow.features.finance.presentation.charts.financeChartCard

import com.jd.financeflow.features.core.presentation.UiText

/**
 * Finance charts view state
 */
data class FinanceChartCardState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val currencyCode: String = "EUR",

    val incomeLineEnable: Boolean = true,
    val expenseLineEnable: Boolean = true,
    val balanceLineEnable: Boolean = true,

    val income: List<Double> = emptyList(),
    val expense: List<Double> = emptyList(),
    val balance: List<Double> = emptyList(),
)
