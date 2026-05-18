package com.jd.financeflow.features.finance.presentation.charts.components

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import ir.ehsannarmani.compose_charts.LineChart

import com.jd.financeflow.R
import com.jd.financeflow.features.finance.domain.models.chart.LineChartData
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line

// Color palette
private val green = Color(0xFF689F38)
private val yellowGreen = Color(0xFF9E9D24)
private val orange = Color(0xFFFFA000)
private val red = Color(0xFFD32F2F)

@Composable
fun FinanceLineChart(
    title: String,
    items: List<LineChartData>,
    maxValue: Float,
    miniValue: Float,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.chart_padding_medium))
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            headlineContent = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        )

        LineChart(
            data = items.map { item ->
                Line(
                    label = item.label,
                    values = item.values.map { it.toDouble() },
                    color = SolidColor(Color(0xFF23af92)),
                    firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                    secondGradientFillColor = Color.Transparent,
                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                    drawStyle = DrawStyle.Stroke(width = 2.dp),
                )
            },
            animationMode = AnimationMode.Together(delayBuilder = { it * 500L }),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FinanceLineChartPReview() {
    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            FinanceLineChart(
                title = "Demo",
                items = listOf(
                    LineChartData(
                        label = "ItemA",
                        values = listOf<Float>(1f, 2f, 3f, 4f, 10f, 50f, 30f, 10f, 5f),
                    )
                ),
                maxValue = 100f,
                miniValue = 0f,
                currencyCode = "EUR",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}