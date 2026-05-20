package com.jd.financeflow.features.finance.presentation.charts.components

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.GridProperties.AxisProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.Line

import com.jd.financeflow.R
import com.jd.financeflow.features.core.presentation.components.formatCurrency
import com.jd.financeflow.features.finance.domain.models.chart.LineChartData


@Composable
fun FinanceLineChart(
    title: String,
    incomes: LineChartData,
    expenses: LineChartData,
    maxValue: Float,
    miniValue: Float,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        shape = RoundedCornerShape(
            dimensionResource(R.dimen.round_corner_size_small)
        ),
        border = BorderStroke(width = 0.dp, color = Color.Transparent),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier
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
                data = listOf(
                    // Income
                    Line(
                        label = incomes.label,
                        values = incomes.values.map { it.toDouble() },
                        color = SolidColor(Color(0xFF0DD254)),
                        firstGradientFillColor = Color(0xFF078D46).copy(alpha = .5f),
                        secondGradientFillColor = Color.Transparent,
                        strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                        gradientAnimationDelay = 1000,
                        drawStyle = DrawStyle.Stroke(width = 2.dp),

                        ),
                    // Expenses
                    Line(
                        label = expenses.label,
                        values = expenses.values.map { it.toDouble() },
                        color = SolidColor(Color(0xFFD71206)),
                        firstGradientFillColor = Color(0xFF980D05).copy(alpha = .5f),
                        secondGradientFillColor = Color.Transparent,
                        strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                        gradientAnimationDelay = 1000,
                        drawStyle = DrawStyle.Stroke(width = 2.dp),
                    )
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    padding = 2.dp,
                    textStyle = TextStyle.Default.copy(fontSize = 12.sp),
                    contentBuilder = { value ->
                        formatCurrency(value, currencyCode, 0)
                    }
                ),
                gridProperties = GridProperties(
                    xAxisProperties = AxisProperties(lineCount = 50),
                    yAxisProperties = AxisProperties(lineCount = 20)
                ),
                minValue = miniValue.toDouble(),
                maxValue = maxValue.toDouble(),
                animationMode = AnimationMode.Together(delayBuilder = { it * 500L }),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FinanceLineChartPReview() {
    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            FinanceLineChart(
                title = "Demo",
                incomes = LineChartData(
                    label = "Income",
                    values = listOf<Float>(50f, 70f, 30f, 4f, 85f, 50f, 30f, 10f, 50f),
                ),
                expenses = LineChartData(
                    label = "Expense",
                    values = listOf<Float>(50f, 20f, 30f, 30f, 50f, 30f, 40f, 10f, 30f),
                ),
                maxValue = 100f,
                miniValue = 0f,
                currencyCode = "EUR",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}