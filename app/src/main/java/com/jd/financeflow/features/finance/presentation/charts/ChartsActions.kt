package com.jd.financeflow.features.finance.presentation.charts

/**
 * Actions that can be performed by the FinanceCharts screen
 */
sealed interface ChartsActions {
    data object OnClearError : ChartsActions
    data object OnRefresh : ChartsActions
}