package com.jd.financeflow.features.finance.presentation.charts.monthlyChart

/**
 * Actions that can be performed by the FinanceCharts screen
 */
sealed interface MonthlyChartActions {
    data object OnClearError : MonthlyChartActions
    data object OnRefresh : MonthlyChartActions
    data class OnToggleChartOption(val index: Int) : MonthlyChartActions
    data class OnSelectYear(val index: Int) : MonthlyChartActions
    data class OnSelectMonth(val index: Int) : MonthlyChartActions

}