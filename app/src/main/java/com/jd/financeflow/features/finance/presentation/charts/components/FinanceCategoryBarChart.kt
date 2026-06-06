package com.jd.financeflow.features.finance.presentation.charts.components

import android.icu.text.NumberFormat
import android.icu.util.Currency
import java.util.Locale
import kotlinx.datetime.Month

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties

import com.jd.financeflow.R

/**
 * Finance bar chart
 * @param title title
 * @param income List of income values for each calendar month, only first 12 values will be utilized
 * @param expense List of expense values for each calendar month, only first 12 values will be utilized
 * @param modifier Compose modifier
 * @param miniValue Minimum value for the y-axis value indication
 * @param currencyCode Currency code for given symbol such as EUR for £
 */
@Composable
fun MonthlyFinanceBarChart(
    title: String,
    income: List<Double>,
    expense: List<Double>,
    modifier: Modifier = Modifier,
    miniValue: Double = 0.0,
    currencyCode: String = "EUR",
) {
    val formatter = NumberFormat.getCurrencyInstance()
    formatter.currency = Currency.getInstance(currencyCode)

    OutlinedCard(
        shape = RoundedCornerShape(
            dimensionResource(R.dimen.chart_round_corner_size_small)
        ),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.chart_padding_medium))
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            ColumnChart(
                data = Month.entries.map { month ->
                    val incomeValue = income.getOrElse(index = month.ordinal) { 0.0 }
                    val expenseValue = expense.getOrElse(index = month.ordinal) { 0.0 }

                    Bars(
                        label = month.name.substring(0, 3),
                        values = listOf(
                            Bars.Data(
                                label = "Income",
                                value = incomeValue,
                                color = Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF1FC931),
                                        Color(0xFF47F84A),
                                        Color(0xFFA1FFA4)
                                    )
                                )
                            ),
                            Bars.Data(
                                label = "Expense",
                                value = expenseValue,
                                color = Brush.verticalGradient(
                                    listOf(
                                        Color(0xFFD01A1A),
                                        Color(0xFFFC4646),
                                        Color(0xFFFFB0B0),
                                    )
                                )
                            )
                        ),
                    )
                },
                barProperties = BarProperties(
                    thickness = dimensionResource(R.dimen.chart_bar_thickness),
                    cornerRadius = Bars.Data.Radius.Rectangle(
                        topLeft = dimensionResource(R.dimen.chart_bar_radius),
                        topRight = dimensionResource(R.dimen.chart_bar_radius)
                    ),
                    spacing = 0.dp
                ),
                labelProperties = LabelProperties(
                    enabled = true,
                    textStyle = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    rotation = LabelProperties.Rotation(
                        mode = LabelProperties.Rotation.Mode.Force,
                        degree = -25f,
                        padding = 0.dp
                    ),
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    padding = 12.dp,
                    textStyle = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
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
                        "${formatter.currency.symbol} $preFormatedValue"
                    }
                ),
                dividerProperties = DividerProperties(enabled = false),
                labelHelperProperties = LabelHelperProperties(enabled = false),
                minValue = miniValue,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FinanceBarChartPreview() {
    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            MonthlyFinanceBarChart(
                title = "Income vs Expense",
                income = listOf(
                    7200.0,
                    7500.0,
                    7300.0,
                    7200.0,
                    7800.0,
                    5000.0,
                    6000.0,
                    7000.0,
                    7000.0,
                    7700.0,
                    7300.0,
                    7900.0
                ),
                expense = listOf(
                    2700.0,
                    3100.0,
                    3100.0,
                    3000.0,
                    3200.0,
                    3500.0,
                    3300.0,
                    2800.0,
                    3700.0,
                    3300.0,
                    3500.0,
                    5000.0
                ),
                modifier = Modifier
                    .height(350.dp)
                    .fillMaxWidth()
                    .padding(innerPadding)
            )
        }
    }
}