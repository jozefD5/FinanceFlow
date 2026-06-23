package com.jd.financeflow.features.finance.presentation.charts

/**
 * Actions that can be performed by the FinanceCharts screen
 */
sealed interface ChartsActions {
    data object OnClearError : ChartsActions
    data object OnRefresh : ChartsActions
    data class OnToggleChartOption(val index: Int) : ChartsActions
    data object OnToggleBalanceRollover : ChartsActions
    data class OnSelectYear(val index: Int) : ChartsActions
    data class OnSelectMonth(val index: Int) : ChartsActions
}