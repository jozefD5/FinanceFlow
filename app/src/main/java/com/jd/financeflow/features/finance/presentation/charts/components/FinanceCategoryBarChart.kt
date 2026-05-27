package com.jd.financeflow.features.finance.presentation.charts.components

import android.content.res.Configuration
import android.icu.util.Currency
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties

import com.jd.financeflow.R
import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.core.presentation.components.formatCurrency
import com.jd.financeflow.features.finance.domain.models.chart.BarChartData


// Color palette
private val green = Color(0xFF36AF42)
private val yellowGreen = Color(0xFF9E9D24)
private val orange = Color(0xFFFFA000)
private val red = Color(0xFFD32F2F)

/**
 * Finance bar chart
 * @param title title
 * @param items List of data to be presented
 * @param maxValue Maximum value available in the list of items. used for y-axis value indication
 * @param miniValue Minimum value for the y-axis value indication
 * @param avgValue Average value of the items
 * @param modifier Compose modifier
 * @param currencyCode Currency code for given symbol such as EUR for £
 */
@Composable
fun FinanceBarChart(
    title: String,
    items: List<BarChartData>,
    maxValue: Float,
    miniValue: Float,
    avgValue: Float,
    modifier: Modifier = Modifier,
    currencyCode: String = "EUR"
) {
    OutlinedCard(
        shape = RoundedCornerShape(
            dimensionResource(R.dimen.chart_round_corner_size_small)
        ),
        border = BorderStroke(width = 0.dp, color = Color.Transparent),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier
    ) {
        val formatter = Currency.getInstance(currencyCode)
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.chart_item_arrangement_spacing)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.chart_padding_medium))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.chart_item_arrangement_spacing))
            ) {
                Text(
                    text = formatter.symbol,
                    color = Color.White,
                    style = TextStyle.Default.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.chart_symbol_size))
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        .wrapContentSize(Alignment.Center)
                )
                Text(
                    text = title,
                    style = TextStyle.Default.copy(fontWeight = FontWeight.Bold)
                )
            }

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.chart_item_arrangement_spacing))
            ) {
                Text(
                    text = stringResource(R.string.chart_average_value_label),
                    style = TextStyle.Default.copy(fontWeight = FontWeight.Medium)
                )

                Text(
                    text = formatCurrency(avgValue, currencyCode),
                    style = TextStyle.Default.copy(fontWeight = FontWeight.Medium)
                )
            }

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

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
                        label = item.label.asString(),
                        values = listOf(
                            Bars.Data(
                                value = item.value.toDouble(),
                                color = dynamicColor
                            )
                        )
                    )
                },
                barProperties = BarProperties(
                    thickness = dimensionResource(R.dimen.chart_bar_thickness),
                    cornerRadius = Bars.Data.Radius.Rectangle(
                        topLeft = dimensionResource(R.dimen.chart_bar_radius),
                        topRight = dimensionResource(R.dimen.chart_bar_radius)
                    ),
                ),
                labelProperties = LabelProperties(
                    enabled = true,
                    builder = { _, label, _, _ ->
                        Text(
                            text = label.substring(0, 3),
                            style = TextStyle.Default.copy(fontSize = 12.sp),
                        )
                    }
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    textStyle = TextStyle.Default.copy(fontSize = 12.sp),
                    contentBuilder = { value -> value.toInt().toString() }
                ),
                dividerProperties = DividerProperties(enabled = false),
                labelHelperProperties = LabelHelperProperties(enabled = false),
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

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun FinanceBarChartPreview() {
    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            FinanceBarChart(
                title = "Income",
                items = listOf(
                    BarChartData(UiText.DynamicString("Salary"), 5_000f),
                    BarChartData(UiText.DynamicString("Bonus"), 4_000f),
                    BarChartData(UiText.DynamicString("Commission"), 3_000f),
                    BarChartData(UiText.DynamicString("Benefit"), 2_000f),
                    BarChartData(UiText.DynamicString("Investment"), 1_000f),
                    BarChartData(UiText.DynamicString("Other"), 500f),
                ),
                maxValue = 5_000f,
                miniValue = 0f,
                avgValue = 2_500f,
                modifier = Modifier
                    .height(400.dp)
                    .fillMaxWidth()
                    .padding(innerPadding)
            )
        }
    }
}