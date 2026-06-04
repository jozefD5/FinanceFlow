package com.jd.financeflow.features.finance.presentation.charts

import com.jd.financeflow.features.core.presentation.UiText

/**
 * Finance charts view state
 */
data class FinanceChartsState(
    // General
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val currencyCode: String = "EUR",

    val incomeCashFlow: List<Double> = emptyList(),
    val averageMonthlyIncome: Double? = null,

    val expenseCashFlow: List<Double> = emptyList(),
    val averageExpenseIncome: Double? = null
)