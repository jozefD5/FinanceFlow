package com.jd.financeflow.features.finance.presentation.charts

import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.finance.domain.models.chart.ChartData

data class FinanceChartsState(
    // General
    val error: UiText? = null,
    val currencyCode: String = "EUR",

    // Income
    val incomeMax: Float = 0f,
    val incomeCategoryChartData: List<ChartData> = emptyList(),
    val incomeCashFlow: List<Float> = emptyList(),

    // Expenses
    val expenseMax: Float = 0f,
    val expenseCategoryChartData: List<ChartData> = emptyList(),
    val expensesCashFlow: List<Float> = emptyList(),
)