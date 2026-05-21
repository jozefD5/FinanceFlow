package com.jd.financeflow.features.finance.presentation.charts

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.jd.financeflow.R
import com.jd.financeflow.features.finance.domain.models.chart.ChartData
import com.jd.financeflow.features.finance.domain.models.chart.LineChartData
import com.jd.financeflow.features.finance.presentation.charts.components.FinanceBarChart
import com.jd.financeflow.features.finance.presentation.charts.components.CashFlowLineChart

@Composable
fun FinanceChartsView(
    state: FinanceChartsState,
    modifier: Modifier = Modifier,
    verticalScrollState: ScrollState = rememberScrollState()
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.chart_padding_medium)),
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(verticalScrollState)
    ) {
//        FinanceBarChart(
//            items = state.incomeCategoryChartData,
//            maxValue = state.incomeMax,
//            miniValue = 0f,
//            currencyCode = state.currencyCode,
//            modifier = Modifier
//                .height(300.dp)
//                .fillMaxWidth()
//                .padding(horizontal = dimensionResource(R.dimen.chart_padding_height))
//        )
//
//        FinanceBarChart(
//            items = state.expenseCategoryChartData,
//            maxValue = state.expenseMax,
//            miniValue = 0f,
//            currencyCode = state.currencyCode,
//            modifier = Modifier
//                .height(300.dp)
//                .fillMaxWidth()
//                .padding(horizontal = dimensionResource(R.dimen.chart_padding_height))
//        )
    }
}


//@Preview(showBackground = true)
//@Composable
//fun FinanceChartsViewPreview() {
//    MaterialTheme {
//        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//            FinanceChartsView(
//                state = FinanceChartsState(
//                    incomeMax = 5_000f,
//                    incomeCategoryChartData = listOf(
//                        ChartData("Salary", 5_000f),
//                        ChartData("Bonus", 4_000f),
//                        ChartData("Commission", 3_000f),
//                        ChartData("Benefit", 2_000f),
//                        ChartData("Investment", 1_000f),
//                        ChartData("Other", 500f),
//                    ),
//                    expenseMax = 2_000f,
//                    expenseCategoryChartData = listOf(
//                        ChartData("Housing", 2_000f),
//                        ChartData("Utility", 250f),
//                        ChartData("Food", 1_000f),
//                        ChartData("Transport", 100f),
//                        ChartData("Health", 100f),
//                        ChartData("Education", 100f),
//                        ChartData("Entertainment", 30f),
//                        ChartData("Other", 100f),
//                    ),
//                ),
//                modifier = Modifier.padding(innerPadding)
//            )
//        }
//    }
//}