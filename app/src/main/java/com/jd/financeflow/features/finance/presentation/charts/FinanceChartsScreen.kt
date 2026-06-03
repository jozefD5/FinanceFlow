package com.jd.financeflow.features.finance.presentation.charts

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.jd.financeflow.R
import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.core.presentation.components.formatCurrency
import com.jd.financeflow.features.finance.domain.models.chart.BarChartData
import com.jd.financeflow.features.finance.domain.models.chart.LineChartData
import com.jd.financeflow.features.finance.presentation.charts.components.FinanceBarChart
import com.jd.financeflow.features.finance.presentation.charts.components.FinanceLineChart
import org.koin.androidx.compose.koinViewModel


@Composable
fun FinanceChartsScreen(
    modifier: Modifier = Modifier,
    viewModel: ChartsViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    FinanceChartsView(
        state = state.value,
        modifier = modifier
    )
}

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
        FinanceLineChart(
            title = "Monthly Income",
            items = listOf(
                LineChartData(
                    label = "Income",
                    color = Color(0xFF12C51A),
                    values = state.incomeCashFlow
                )
            ),
            miniValue = 0.0,
            labels = listOf("1", "6", "11", "16", "21", "26", "31"),
            currencyCode = state.currencyCode,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 350.dp)
        )

        FinanceLineChart(
            title = "Monthly Expenses",
            items = listOf(
                LineChartData(
                    label = "Expense",
                    color = Color(0xFFBE0E1F),
                    values = state.expensesCashFlow
                )
            ),
            miniValue = 0.0,
            labels = listOf("1", "6", "11", "16", "21", "26", "31"),
            currencyCode = state.currencyCode,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 350.dp)
        )

//        FinanceBarChart(
//            title = stringResource(R.string.finance_type_income),
//            items = state.incomeCategoryChartData,
//            maxValue = state.maxIncome,
//            miniValue = 0f,
//            currencyCode = state.currencyCode,
//            subTitle = "${stringResource(R.string.chart_average_value_label)}: ${
//                formatCurrency(
//                    state.averageIncome,
//                    state.currencyCode
//                )
//            }",
//            modifier = Modifier
//                .height(400.dp)
//                .fillMaxWidth()
//                .padding(dimensionResource(R.dimen.chart_padding_large))
//        )
    }
}

@Preview(showBackground = true)
@Composable
fun FinanceChartsViewPreview() {
    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            FinanceChartsView(
                state = FinanceChartsState(),
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}