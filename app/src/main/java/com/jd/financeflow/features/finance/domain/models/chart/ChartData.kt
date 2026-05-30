package com.jd.financeflow.features.finance.domain.models.chart

import androidx.compose.ui.graphics.Color
import com.jd.financeflow.features.core.presentation.UiText

/**
 * Chart single data representation point.
 */
data class BarChartData(
    val label: UiText,
    val value: Float,
)

/**
 * Line chart data representing grouped sequence of data
 */
data class LineChartData(
    val label: String,
    val color: Color,
    val values: List<Double> = emptyList(),
)

