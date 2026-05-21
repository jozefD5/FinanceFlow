package com.jd.financeflow.features.finance.presentation.charts

/**
 * Actions that can be performed by the FinanceCharts screen
 */
sealed interface FinanceChartsActions {
    data object OnClearError : FinanceChartsActions
}