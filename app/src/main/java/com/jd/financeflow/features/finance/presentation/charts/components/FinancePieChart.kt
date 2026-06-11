package com.jd.financeflow.features.finance.presentation.charts.components

import android.icu.text.NumberFormat
import android.icu.util.Currency
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties

import com.jd.financeflow.R
import com.jd.financeflow.features.finance.domain.models.chart.PieChartData


@Composable
fun FinancePieChart(
    title: String,
    items: List<PieChartData>,
    onItemClick: (PieChartData) -> Unit,
    modifier: Modifier = Modifier,
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
            Box(
                contentAlignment = androidx.compose.ui.Alignment.Center,
                modifier = modifier.fillMaxSize()
            ) {
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
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    colorAnimEnterSpec = tween(300),
                    colorAnimExitSpec = tween(300),
                    scaleAnimExitSpec = tween(300),
                    spaceDegreeAnimExitSpec = tween(300),
                    spaceDegree = 1f,
                    selectedPaddingDegree = 4f,
                    labelHelperProperties = LabelHelperProperties(enabled = false),
                    style = Pie.Style.Stroke(),
                    modifier = Modifier.fillMaxSize(),
                )

                Text(
                    text = "Total: ${formatter.currency.symbol}${items.sumOf { it.data }.toInt()}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
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
                        label = "Salery",
                        color = Color(0xFF12C51A),
                        selectedColor = MaterialTheme.colorScheme.inversePrimary,
                        data = 5000.0,
                        selected = false
                    ),
                    PieChartData(
                        label = "Investment",
                        color = Color(0xFF12C592),
                        selectedColor = MaterialTheme.colorScheme.inversePrimary,
                        data = 2500.0,
                        selected = false
                    ),
                    PieChartData(
                        label = "Freelance",
                        color = Color(0xFF088343),
                        selectedColor = MaterialTheme.colorScheme.inversePrimary,
                        data = 1000.0,
                        selected = false
                    ),
                    PieChartData(
                        label = "Other",
                        color = Color(0xFF2196F3),
                        selectedColor = MaterialTheme.colorScheme.inversePrimary,
                        data = 1000.0,
                        selected = false
                    )
                ),
                onItemClick = {},
                modifier = Modifier
                    .padding(innerPadding)
                    .heightIn(max = 250.dp)
            )
        }
    }
}