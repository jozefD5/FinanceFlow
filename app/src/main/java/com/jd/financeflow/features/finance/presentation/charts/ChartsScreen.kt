package com.jd.financeflow.features.finance.presentation.charts

import java.time.Month

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalLocale
import org.koin.androidx.compose.koinViewModel

import com.jd.financeflow.R
import com.jd.financeflow.features.finance.domain.models.chart.LineChartData
import com.jd.financeflow.features.finance.presentation.charts.components.FinanceLineChart

@Composable
fun ChartsScreen(
    modifier: Modifier = Modifier,
    viewModel: ChartsViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    ChartsView(
        state = state.value,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
fun ChartsView(
    state: ChartsState,
    onAction: (ChartsActions) -> Unit,
    modifier: Modifier = Modifier,
    verticalScrollState: ScrollState = rememberScrollState()
) {
    val lineLabels = listOf("1", "6", "11", "16", "21", "26", "31")

    PullToRefreshBox(
        isRefreshing = state.isLoading,
        onRefresh = { onAction(ChartsActions.OnRefresh) },
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.chart_padding_medium)),
            modifier = modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
                .verticalScroll(verticalScrollState)
        ) {
            FinanceLineChart(
                title = stringResource(R.string.chart_annual_cash_flow_title),
                items = listOf(
                    LineChartData(
                        label = stringResource(R.string.finance_type_expense),
                        color = Color(0xFF12C51A),
                        values = state.yearlyCashFlowIncome
                    ),
                    LineChartData(
                        label = stringResource(R.string.finance_type_expense),
                        color = Color(0xFFBE0E1F),
                        values = state.yearlyCashFlowExpense
                    ),
                    LineChartData(
                        label = stringResource(R.string.finance_balance_title),
                        color = Color(0xFF0C6EE5),
                        values = state.avgMonthlyExpense
                    )
                ),
                miniValue = state.yearlyCashFlowBalance.minOrNull() ?: 0.0,
                labels = Month.entries.map {
                    it.name.substring(0, 3).lowercase(LocalLocale.current.platformLocale)
                },
                currencyCode = state.currencyCode,
                enableLabelHelperProperties = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp)
            )

            FinanceLineChart(
                title = stringResource(R.string.chart_monthly_income_title),
                items = listOf(
                    LineChartData(
                        label = stringResource(R.string.finance_type_income),
                        color = Color(0xFF12C51A),
                        values = state.avgMonthlyIncome
                    )
                ),
                miniValue = 0.0,
                labels = lineLabels,
                currencyCode = state.currencyCode,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp)
            )

            FinanceLineChart(
                title = stringResource(R.string.chart_monthly_expenses_title),
                items = listOf(
                    LineChartData(
                        label = stringResource(R.string.finance_type_expense),
                        color = Color(0xFFBE0E1F),
                        values = state.avgMonthlyExpense
                    )
                ),
                miniValue = 0.0,
                labels = lineLabels,
                currencyCode = state.currencyCode,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChartsViewPreview() {
    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            ChartsView(
                state = ChartsState(),
                onAction = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}