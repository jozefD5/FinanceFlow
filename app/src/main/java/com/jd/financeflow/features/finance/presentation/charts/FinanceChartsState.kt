package com.jd.financeflow.features.finance.presentation.charts

import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.finance.domain.models.chart.ChartData


data class FinanceChartsState(
    // General
    val error: UiText? = null,
    val currencyCode: String = "EUR",

    // Income
    val incomeMax: Float = 0f,
    val incomeChartData: List<ChartData> = emptyList(),

    // Expenses
    val expenseMax: Float = 0f,
    val expenseChartData: List<ChartData> = emptyList()
)