package com.jd.financeflow.features.finance.presentation.charts.components

//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.material3.ListItem
//import androidx.compose.material3.ListItemDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.dimensionResource
//
//import ir.ehsannarmani.compose_charts.LineChart
//
//import com.jd.financeflow.R
//import com.jd.financeflow.features.finance.domain.models.chart.ChartData
//import ir.ehsannarmani.compose_charts.models.Bars
//import ir.ehsannarmani.compose_charts.models.Line
//
//// Color palette
//private val green = Color(0xFF689F38)
//private val yellowGreen = Color(0xFF9E9D24)
//private val orange = Color(0xFFFFA000)
//private val red = Color(0xFFD32F2F)
//
//@Composable
//fun FinanceLineChart(
//    title: String,
//    items: List<ChartData>,
//    maxValue: Float,
//    miniValue: Float,
//    currencyCode: String,
//    modifier: Modifier = Modifier,
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(dimensionResource(R.dimen.chart_padding_medium))
//    ) {
//        ListItem(
//            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
//            headlineContent = {
//                Text(
//                    text = title,
//                    style = MaterialTheme.typography.titleMedium
//                )
//            }
//        )
//
//        LineChart(
//            data = items.map { item ->
//                val fraction: Float = if (maxValue > 0) (item.value / maxValue) else 0f
//                val dynamicColor = Brush.verticalGradient(
//                    colors = when {
//                        fraction > 0.75f -> listOf(red, orange, yellowGreen, green)
//                        fraction > 0.50f -> listOf(orange, yellowGreen, green)
//                        fraction > 0.25f -> listOf(yellowGreen, green)
//                        else -> listOf(green, green)
//                    }
//                )
//                Line(
//                    label = item.label,
//                    values = listOf(
//                        Bars.Data(
//                            value = item.value.toDouble(),
//                            color = dynamicColor
//                        )
//                    )
//                )
//            },
//            modifier = Modifier.fillMaxSize()
//        )
//
//    }
//
//
//}