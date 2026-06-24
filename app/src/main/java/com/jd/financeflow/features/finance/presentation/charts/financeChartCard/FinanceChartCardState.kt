package com.jd.financeflow.features.finance.presentation.charts.financeChartCard

import com.jd.financeflow.features.core.presentation.UiText

/**
 * Finance charts view state
 */
data class FinanceChartCardState(
    val error: UiText? = null,
    val currencyCode: String = "EUR",
    val title: String = "",

    val incomeLineEnable: Boolean = true,
    val expenseLineEnable: Boolean = true,
    val balanceLineEnable: Boolean = true,

    val avgMonthlyIncome: List<Double> = emptyList(),
    val avgMonthlyExpense: List<Double> = emptyList(),
    val avgMonthlyBalance: List<Double> = emptyList(),
)
