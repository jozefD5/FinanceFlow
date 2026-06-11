package com.jd.financeflow.features.finance.domain.models.chart

import androidx.compose.ui.graphics.Color

/**
 * Line chart data representing grouped sequence of data
 */
data class LineChartData(
    val label: String,
    val color: Color,
    val values: List<Double> = emptyList(),
    val enable: Boolean = true
)

/**
 * Pie chart data representing single piece of data
 */
data class PieChartData(
    val label: String,
    val color: Color,
    val selectedColor: Color,
    val data: Double,
    val selected: Boolean = false,
    val enable: Boolean = true
)
