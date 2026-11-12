package com.jd.financeflow.features.charts.presentation.financeChartCard

/**
 * Actions that can be performed by the FinanceCharts screen
 */
sealed interface FinanceChartCardActions {
    data object OnClearError : FinanceChartCardActions
    data object OnRefresh : FinanceChartCardActions
}