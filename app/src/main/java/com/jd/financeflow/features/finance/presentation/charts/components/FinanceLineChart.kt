package com.jd.financeflow.features.finance.presentation.charts.components

import java.util.Locale

import android.icu.text.NumberFormat
import android.icu.util.Currency
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties

import com.jd.financeflow.features.finance.domain.models.chart.LineChartData
import ir.ehsannarmani.compose_charts.models.PopupProperties

/**
 * Finance linear charts for displaying different finance data such as income and expenses
 *
 * @param items Items (chart data) to display, each item represents single line of data
 * @param modifier composable modifier
 * @param labels optional X-axis labels
 * @param currencyCode currency code, e.g. "EUR" for Euro
 * @param enableLabelHelperProperties or disable label helper properties
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FinanceLineChart(
    items: List<LineChartData>,
    modifier: Modifier = Modifier,
    labels: List<String>? = null,
    currencyCode: String = "EUR",
    enableLabelHelperProperties: Boolean = false
) {
    val formatter = NumberFormat.getCurrencyInstance()
    formatter.currency = Currency.getInstance(currencyCode)

    LineChart(
        data = items.mapNotNull { item ->
            if (item.enable) {
                Line(
                    label = item.label,
                    values = item.values,
                    color = SolidColor(item.color),
                    firstGradientFillColor = item.color.copy(alpha = 0.3f),
                    secondGradientFillColor = item.color.copy(alpha = 0.1f),
                    strokeAnimationSpec = tween(200, easing = EaseInOutCubic),
                    drawStyle = DrawStyle.Stroke(width = 2.dp),
                    curvedEdges = true,
                )
            } else {
                null
            }
        },
        indicatorProperties = HorizontalIndicatorProperties(
            padding = 12.dp,
            textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            contentBuilder = { value ->
                val preFormatedValue = when {
                    value >= 1_000_000 -> String.format(
                        Locale.getDefault(),
                        "%.1fM",
                        value / 1_000_000
                    ).replace(".0", "")

                    value >= 1_000 -> String.format(
                        Locale.getDefault(),
                        "%.1fK",
                        value / 1_000
                    ).replace(".0", "")

                    else -> value.toInt().toString()
                }
                "${formatter.currency.symbol}$preFormatedValue"
            }),
        labelProperties = LabelProperties(
            enabled = !labels.isNullOrEmpty(),
            labels = labels ?: emptyList(),
            textStyle = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            rotation = LabelProperties.Rotation(
                mode = LabelProperties.Rotation.Mode.Force,
                degree = -25f,
                padding = 0.dp
            )
        ),
        labelHelperProperties = LabelHelperProperties(
            enabled = enableLabelHelperProperties,
            textStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        ),
        gridProperties = GridProperties(
            enabled = true,
            xAxisProperties = GridProperties.AxisProperties(
                enabled = true,
                color = SolidColor(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ),
            yAxisProperties = GridProperties.AxisProperties(
                enabled = false
            )
        ),
        popupProperties = PopupProperties(enabled = false),
        animationDelay = 0,
        animationMode = AnimationMode.None,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun FinanceLineChartPreview() {
    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            FinanceLineChart(
                items = listOf(
                    LineChartData(
                        label = "Income",
                        color = Color(0xFF12C51A),
                        values = listOf(
                            0.0, 100.0, 0.0, 0.0, 2500.0, 50.0, 0.0, 4500.0, 4000.0, 500.0, 2000.0
                        ),
                    ),
                    LineChartData(
                        label = "Expense",
                        color = Color(0xFFBE0E1F),
                        values = listOf(
                            3000.0, 200.0, 60.0, 100.0, 15.0, 40.0, 40.0, 100.0, 0.0, 0.0, 1000.0
                        ),
                    ),
                ),
                labels = listOf("1", "6", "11", "16", "21", "26", "31"),
                currencyCode = "EUR",
                modifier = Modifier
                    .padding(innerPadding)
                    .heightIn(max = 350.dp)
            )
        }
    }
}
