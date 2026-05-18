package com.jd.financeflow.features.finance.domain.models.chart

/**
 * Chart single data representation point.
 */
data class ChartData(
    val label: String,
    val value: Float,
)

/**
 * Line chart data representing grouped sequence of data
 */
data class LineChartData(
    val label: String,
    val values: List<Float> = emptyList(),
)

