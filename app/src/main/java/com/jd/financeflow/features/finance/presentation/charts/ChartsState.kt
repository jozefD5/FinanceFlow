package com.jd.financeflow.features.finance.presentation.charts

import com.jd.financeflow.features.core.presentation.UiText

/**
 * Finance charts view state
 */
data class ChartsState(
    // General
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val currencyCode: String = "EUR",
    val selectedChartOptions: List<Boolean> = listOf(true, true, true),
    val enableCascadeBalance: Boolean = false,

    val avgMonthlyIncome: List<Double> = emptyList(),
    val avgMonthlyExpense: List<Double> = emptyList(),
    val avgMonthlyBalance: List<Double> = emptyList(),

    val yearlyCashFlowIncome: List<Double> = emptyList(),
    val yearlyCashFlowExpense: List<Double> = emptyList(),
    val yearlyCashFlowBalance: List<Double> = emptyList(),

    val incomeCategories: List<Pair<UiText, Double>> = emptyList(),
    val expenseCategories: List<Pair<UiText, Double>> = emptyList(),
)
