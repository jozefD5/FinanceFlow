package com.jd.financeflow.features.finance.presentation.charts.monthlyChart

import androidx.compose.foundation.layout.Arrangement
import java.time.LocalDate
import kotlinx.datetime.format.MonthNames

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.jd.financeflow.features.core.presentation.components.LargeDropdownMenu
import com.jd.financeflow.features.finance.domain.models.chart.LineChartData
import com.jd.financeflow.features.finance.presentation.charts.components.FinanceLineChart
import org.koin.androidx.compose.koinViewModel

// Monthly finance line chart labels
private val lineLabels = listOf("1", "6", "11", "16", "21", "26", "31")


@Composable
fun MonthlyChartScreen(
    viewModel: MonthlyChartViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsStateWithLifecycle()


}

/**
 * Finance charts view for monthly finance data
 *
 * @param state View state
 * @param modifier Modifier
 */
@Composable
fun MonthlyChartView(
    state: MonthlyChartState,
    modifier: Modifier = Modifier,
) {
    FinanceLineChart(
        title = stringResource(R.string.chart_monthly_cash_flow_title),
        items = listOf(
            LineChartData(
                label = stringResource(R.string.finance_type_income),
                color = Color(0xFF12C51A),
                values = state.avgMonthlyIncome,
                enable = state.selectedChartOptions[0]
            ),
            LineChartData(
                label = stringResource(R.string.finance_type_expense),
                color = Color(0xFFBE0E1F),
                values = state.avgMonthlyExpense,
                enable = state.selectedChartOptions[1]
            ),
            LineChartData(
                label = stringResource(R.string.finance_balance_title),
                color = Color(0xFF0C6EE5),
                values = state.avgMonthlyBalance,
                enable = state.selectedChartOptions[2]
            ),
        ),
        labels = lineLabels,
        currencyCode = state.currencyCode,
        modifier = modifier
    )
}

@Preview
@Composable
fun MonthlyChartViewPreview() {
    MaterialTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) { innerPadding ->
            MonthlyChartView(
                state = MonthlyChartState(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .heightIn(max = 450.dp)
            )
        }
    }
}