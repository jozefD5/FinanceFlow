package com.jd.financeflow.features.finance.presentation.charts


data class ChartData (
    val label: String,
    val value: Double,
)

data class FinanceChartsState(
    val dataListWeek: List<ChartData> = emptyList(),
)