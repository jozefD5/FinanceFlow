package com.jd.financeflow.features.finance.presentation.charts.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties

import com.jd.financeflow.R
import com.jd.financeflow.features.core.presentation.components.formatCurrency
import com.jd.financeflow.features.finance.domain.models.chart.ChartData

// Color palette
private val green = Color(0xFF689F38)
private val yellowGreen = Color(0xFF9E9D24)
private val orange = Color(0xFFFFA000)
private val red = Color(0xFFD32F2F)

/**
 * Finance category bar chart
 * @param title Title
 * @param items List of data to be presented
 * @param maxValue Maximum value available in the list of items. used for y-axis value indication
 * @param miniValue Minimum value for the y-axis value indication
 * @param currencyCode Currency format by y-axis, e.g. EUR for euro
 * @param modifier Compose modifier
 */
@Composable
fun FinanceCategoryBarChart(
    title: String,
    items: List<ChartData>,
    maxValue: Float,
    miniValue: Float,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        shape = RoundedCornerShape(
            dimensionResource(R.dimen.padding_medium)
        ),
        border = BorderStroke(width = 0.dp, color = Color.Transparent),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.list_items_spacing_medium)),
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_large))
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
                            else -> listOf(green, green.copy(alpha = 0.8f))
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
                    cornerRadius = Bars.Data.Radius.Rectangle(topLeft = 12.dp, topRight = 12.dp),
                    spacing = 1.dp,
                ),
                labelProperties = LabelProperties(
                    enabled = true,
                    padding = 6.dp,
                    rotation = LabelProperties.Rotation(degree = 0f)
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    padding = 6.dp,
                    textStyle = TextStyle.Default.copy(fontSize = 12.sp),
                    contentBuilder = { value ->
                        //"£${value.format(0)}"
                        formatCurrency(value, currencyCode, 0)
                    }
                ),
                dividerProperties = DividerProperties(enabled = false),
                labelHelperProperties = LabelHelperProperties(enabled = false),
                gridProperties = GridProperties(enabled = true),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                maxValue = maxValue + (maxValue * 0.1),
                minValue = miniValue.toDouble(),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 6.dp)
            )
        }
    }

}