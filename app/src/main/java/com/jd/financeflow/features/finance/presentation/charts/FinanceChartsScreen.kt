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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.jd.financeflow.R
import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.finance.domain.models.chart.BarChartData
import com.jd.financeflow.features.finance.presentation.charts.components.FinanceBarChart

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
        FinanceBarChart(
            title = stringResource(R.string.finance_type_income),
            items = state.incomeCategoryChartData,
            maxValue = state.maxIncome,
            miniValue = 0f,
            avgValue = state.averageIncome,
            currencyCode = state.currencyCode,
            modifier = Modifier
                .height(400.dp)
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.chart_padding_large))
        )

        FinanceBarChart(
            title = stringResource(R.string.finance_type_expense),
            items = state.expensesCategoryChartData,
            maxValue = state.maxExpenses,
            miniValue = 0f,
            avgValue = state.averageExpenses,
            currencyCode = state.currencyCode,
            modifier = Modifier
                .height(400.dp)
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.chart_padding_large))
        )

    }
}


@Preview(showBackground = true)
@Composable
fun FinanceChartsViewPreview() {
    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            FinanceChartsView(
                state = FinanceChartsState(
                    maxIncome = 5_000f,
                    averageIncome = 2_583.33f,
                    incomeCategoryChartData = listOf(
                        BarChartData(UiText.DynamicString("Salary"), 5_000f),
                        BarChartData("Bonus", 4_000f),
                        BarChartData("Commission", 3_000f),
                        BarChartData("Benefit", 2_000f),
                        BarChartData("Investment", 1_000f),
                        BarChartData("Other", 500f),
                    ),
                    maxExpenses = 2_000f,
                    averageExpenses = 556.25f,
                    expensesCategoryChartData = listOf(
                        BarChartData("Housing", 2_000f),
                        BarChartData("Utility", 250f),
                        BarChartData("Food", 800f),
                        BarChartData("Transport", 500f),
                        BarChartData("Health", 100f),
                        BarChartData("Education", 500f),
                        BarChartData("Entertainment", 100f),
                        BarChartData("Other", 200f),
                    ),
                ),
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}