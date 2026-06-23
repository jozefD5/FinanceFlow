package com.jd.financeflow.features.finance.presentation.charts.monthlyChart

import com.jd.financeflow.features.core.presentation.UiText

/**
 * Finance charts view state
 */
data class MonthlyChartState(
    val error: UiText? = null,
    val currencyCode: String = "EUR",
    val selectedChartOptions: List<Boolean> = listOf(true, true, true),

    val selectedYearIndex: Int = 0,
    val selectedMonthIndex: Int = 0,

    val avgMonthlyIncome: List<Double> = emptyList(),
    val avgMonthlyExpense: List<Double> = emptyList(),
    val avgMonthlyBalance: List<Double> = emptyList(),
)
