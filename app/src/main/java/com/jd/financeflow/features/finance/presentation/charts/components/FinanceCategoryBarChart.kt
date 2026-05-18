package com.jd.financeflow.features.finance.presentation.charts.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.GridProperties.AxisProperties

import com.jd.financeflow.R
import com.jd.financeflow.features.core.presentation.components.formatCurrency
import com.jd.financeflow.features.finance.domain.models.chart.ChartData


// Color palette
private val green = Color(0xFF689F38)
private val yellowGreen = Color(0xFF9E9D24)
private val orange = Color(0xFFFFA000)
private val red = Color(0xFFD32F2F)

/**
 * Finance bar chart
 * @param title Title
 * @param items List of data to be presented
 * @param maxValue Maximum value available in the list of items. used for y-axis value indication
 * @param miniValue Minimum value for the y-axis value indication
 * @param currencyCode Currency format by y-axis, e.g. EUR for euro
 * @param modifier Compose modifier
 */
@Composable
fun FinanceBarChart(
    title: String,
    items: List<ChartData>,
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

            ColumnChart(
                data = items.map { item ->
                    val fraction: Float = if (maxValue > 0) (item.value / maxValue) else 0f
                    val dynamicColor = Brush.verticalGradient(
                        colors = when {
                            fraction > 0.75f -> listOf(red, orange, yellowGreen, green)
                            fraction > 0.50f -> listOf(orange, yellowGreen, green)
                            fraction > 0.25f -> listOf(yellowGreen, green)
                            else -> listOf(green, green)
                        }
                    )
                    Bars(
                        label = item.label,
                        values = listOf(
                            Bars.Data(
                                value = item.value.toDouble(),
                                color = dynamicColor
                            )
                        )
                    )
                },
                barProperties = BarProperties(
                    thickness = 32.dp,
                    cornerRadius = Bars.Data.Radius.Rectangle(topLeft = 14.dp, topRight = 14.dp),
                    spacing = 1.dp,
                ),
                labelProperties = LabelProperties(
                    enabled = true,
                    builder = { _, label, _, _ ->
                        Text(
                            text = label.substring(0, 3),
                            style = TextStyle.Default.copy(
                                fontSize = 12.sp,
                            ),
                        )
                    }
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    padding = 2.dp,
                    textStyle = TextStyle.Default.copy(fontSize = 12.sp),
                    contentBuilder = { value ->
                        formatCurrency(value, currencyCode, 0)
                    }
                ),
                dividerProperties = DividerProperties(enabled = false),
                labelHelperProperties = LabelHelperProperties(enabled = false),
                gridProperties = GridProperties(
                    xAxisProperties = AxisProperties(lineCount = 7),
                    yAxisProperties = AxisProperties(lineCount = 7)
                ),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                maxValue = maxValue + (maxValue * 0.05),
                minValue = miniValue.toDouble(),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FinanceBarChartPreview() {
    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            FinanceBarChart(
                title = "Demo",
                items = listOf(
                    ChartData("Salary", 5_000f),
                    ChartData("Bonus", 4_000f),
                    ChartData("Commission", 3_000f),
                    ChartData("Benefit", 2_000f),
                    ChartData("Investment", 1_000f),
                    ChartData("Other", 500f),
                ),
                maxValue = 5_000f,
                miniValue = 0f,
                currencyCode = "EUR",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}