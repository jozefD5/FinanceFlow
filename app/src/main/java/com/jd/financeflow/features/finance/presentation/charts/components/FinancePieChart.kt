package com.jd.financeflow.features.finance.presentation.charts.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp

import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties

import com.jd.financeflow.R
import com.jd.financeflow.features.core.presentation.components.formatCurrency
import com.jd.financeflow.features.finance.domain.models.chart.PieChartData
import com.jd.financeflow.features.finance.presentation.home.components.FinancialProportionBarData
import com.jd.financeflow.features.finance.presentation.home.components.LinearProgressWidgetList

/**
 * Finance pie chart for displaying different finance data such as income and expenses
 *
 * @param title Title
 * @param items Items (chart data) to display, each item represents single pie slice
 * @param onItemClick Item click handler
 * @param modifier Modifier
 * @param currencyCode currency code, e.g. "EUR" for Euro
 */
@Composable
fun FinancePieChart(
    title: String,
    items: List<PieChartData>,
    onItemClick: (PieChartData) -> Unit,
    modifier: Modifier = Modifier,
    currencyCode: String = "EUR",
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMediumEmphasized,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_small))
        )

        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .weight(0.6f)
        ) {
            val chartSize = min(maxWidth, maxHeight)

            // Scaling stroke width between 16dp and 40dp based on available size
            val strokeWidth = (chartSize * 0.15f).coerceIn(16.dp, 40.dp)

            PieChart(
                data = items.mapNotNull { item ->
                    if (item.enable) {
                        Pie(
                            label = item.label,
                            data = item.data,
                            color = item.color,
                            selectedColor = item.selectedColor,
                            selected = item.selected
                        )
                    } else {
                        null
                    }
                },
                onPieClick = { pie ->
                    onItemClick(items[items.indexOf(items.find { it.label == pie.label })])
                },
                selectedScale = 1.1f,
                scaleAnimEnterSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
                ),
                colorAnimEnterSpec = tween(300),
                colorAnimExitSpec = tween(300),
                scaleAnimExitSpec = tween(300),
                spaceDegreeAnimExitSpec = tween(300),
                spaceDegree = 4f,
                selectedPaddingDegree = 4f,
                labelHelperProperties = LabelHelperProperties(enabled = false),
                style = Pie.Style.Stroke(width = strokeWidth),
                modifier = Modifier.size(chartSize),
            )

            // Heuristic for font size: base size is ~10% of chart diameter
            val baseFontSize = chartSize.value * 0.1f

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(strokeWidth) // Ensure text stays within the hole
            ) {
                Text(
                    text = stringResource(R.string.chart_total_title),
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = (baseFontSize * 0.7f).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = formatCurrency(
                        items.sumOf { it.data }, currencyCode
                    ),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    fontSize = baseFontSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        LinearProgressWidgetList(
            items = items.map { item ->
                item.toFinancialProportionBarData(
                    total = items.sumOf { it.data },
                    currencyCode = currencyCode
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
        )
    }
}


fun PieChartData.toFinancialProportionBarData(
    total: Double,
    currencyCode: String,
): FinancialProportionBarData {
    return FinancialProportionBarData(
        label = label,
        value = data,
        maxValue = total,
        color = color,
        currencyCode = currencyCode
    )
}

@Preview(showBackground = true)
@Composable
fun FinancePieChartPreview() {
    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            FinancePieChart(
                title = "Income",
                items = listOf(
                    PieChartData(
                        label = "Salary",
                        color = Color(0xFF12C51A),
                        selectedColor = MaterialTheme.colorScheme.inversePrimary,
                        data = 5000.0,
                        selected = false
                    ), PieChartData(
                        label = "Investment",
                        color = Color(0xFF12C592),
                        selectedColor = MaterialTheme.colorScheme.inversePrimary,
                        data = 2500.0,
                        selected = false
                    ), PieChartData(
                        label = "Freelance",
                        color = Color(0xFF088343),
                        selectedColor = MaterialTheme.colorScheme.inversePrimary,
                        data = 1000.0,
                        selected = false
                    ), PieChartData(
                        label = "Other",
                        color = Color(0xFF2196F3),
                        selectedColor = MaterialTheme.colorScheme.inversePrimary,
                        data = 1000.0,
                        selected = false
                    )
                ),
                onItemClick = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

















