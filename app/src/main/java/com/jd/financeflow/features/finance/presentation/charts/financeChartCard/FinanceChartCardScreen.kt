package com.jd.financeflow.features.finance.presentation.charts.financeChartCard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.jd.financeflow.R
import com.jd.financeflow.features.finance.domain.models.chart.LineChartData
import com.jd.financeflow.features.finance.presentation.charts.components.FinanceLineChart

// Monthly finance line chart labels
private val lineLabels = listOf("1", "6", "11", "16", "21", "26", "31")


@Composable
fun FinanceChartCard(
    state: FinanceChartCardState,
    modifier: Modifier = Modifier,
) {
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
                text = state.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            FinanceLineChart(
                items = listOf(
                    LineChartData(
                        label = stringResource(R.string.finance_type_income),
                        color = Color(0xFF12C51A),
                        values = state.avgMonthlyIncome
                    ),
                    LineChartData(
                        label = stringResource(R.string.finance_type_expense),
                        color = Color(0xFFBE0E1F),
                        values = state.avgMonthlyExpense
                    ),
                    LineChartData(
                        label = stringResource(R.string.finance_balance_title),
                        color = Color(0xFF0C6EE5),
                        values = state.avgMonthlyBalance
                    ),
                ),
                labels = lineLabels,
                currencyCode = state.currencyCode,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun FinanceChartCardPreview() {
    MaterialTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) { innerPadding ->
            FinanceChartCard(
                state = FinanceChartCardState(
                    title = "Jan 2026"
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .heightIn(max = 250.dp)
            )
        }
    }
}