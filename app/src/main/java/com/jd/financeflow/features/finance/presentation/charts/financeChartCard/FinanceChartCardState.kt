package com.jd.financeflow.features.finance.presentation.charts.financeChartCard

import com.jd.financeflow.features.core.presentation.UiText

/**
 * Finance charts view state
 */
data class FinanceChartCardState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val currencyCode: String = "EUR",

    val dataAvailable: Boolean = false,
    val balance: Double = 0.0,
    val incomeTransactions: List<Double> = emptyList(),
    val expenseTransactions: List<Double> = emptyList(),
    val balanceLevel: List<Double> = emptyList(),

    val incomeCategories: List<Pair<UiText, Double>> = emptyList(),
    val expenseCategories: List<Pair<UiText, Double>> = emptyList(),
)
