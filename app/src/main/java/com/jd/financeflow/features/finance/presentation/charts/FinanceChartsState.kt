package com.jd.financeflow.features.finance.presentation.charts

import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.finance.domain.models.chart.BarChartData

/**
 * Finance charts view state
 */
data class FinanceChartsState(
    // General
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val currencyCode: String = "EUR",

    // Income
//    val maxIncome: Float = 0f,
//    val averageIncome: Float = 0f,
//    val incomeCategoryChartData: List<BarChartData> = emptyList(),

)