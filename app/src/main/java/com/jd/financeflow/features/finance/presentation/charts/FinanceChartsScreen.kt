package com.jd.financeflow.features.finance.presentation.charts

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle

@Composable
fun FinanceChartsView(
    state: FinanceChartsState,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColumnChart(
                modifier = Modifier
                    .heightIn(300.dp)
                    .width(800.dp)
                    .fillMaxWidth()
                    .padding(22.dp),
                data = state.dataListWeek.map { data ->
                    Bars(
                        label = data.label,
                        values = listOf(
                            Bars.Data(
                                value = data.value,
                                color = SolidColor(MaterialTheme.colorScheme.primary)
                            )
                        ),
                    )
                },
                barProperties = BarProperties(
                    cornerRadius = Bars.Data.Radius.Rectangle(topRight = 8.dp, topLeft = 8.dp),
                    spacing = 4.dp,
                    thickness = 24.dp
                ),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                minValue = 0.0,
                maxValue = (state.dataListWeek.maxOf { it.value }) * 1.10,
                labelProperties = LabelProperties(enabled = false),
                dividerProperties = DividerProperties(
                    enabled = false,
                    xAxisProperties = LineProperties(
                        enabled = false,
                    ),
                    yAxisProperties = LineProperties(
                        enabled = false,
                    )
                ),

                )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FinanceChartsViewPreview() {
    MaterialTheme {
        Scaffold { innerPadding ->
            FinanceChartsView(
                state = FinanceChartsState(
                    dataListWeek = listOf(
                        ChartData("Mon", 10.0),
                        ChartData("Tue", 20.0),
                        ChartData("Wed", 30.0),
                        ChartData("Thu", 40.0),
                        ChartData("Fri", 50.0),
                        ChartData("Sat", 60.0),
                        ChartData("Sun", 70.0),

                        ChartData("Mon", 10.0),
                        ChartData("Tue", 20.0),
                        ChartData("Wed", 30.0),
                        ChartData("Thu", 40.0),
                        ChartData("Fri", 50.0),
                        ChartData("Sat", 60.0),
                        ChartData("Sun", 70.0),
                    )
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}