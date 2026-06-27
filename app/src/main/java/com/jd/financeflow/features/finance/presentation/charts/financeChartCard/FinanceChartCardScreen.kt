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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.jd.financeflow.R
import com.jd.financeflow.features.finance.domain.models.chart.LineChartData
import com.jd.financeflow.features.finance.presentation.charts.components.FinanceLineChart

// Monthly finance line chart labels
private val lineLabels = listOf("1", "6", "11", "16", "21", "26", "31")

/**
 * Finance chart card
 *
 * @param title Title
 * @param modifier Modifier
 * @param viewModel View model
 */
@Composable
fun FinanceChartCard(
    title: String,
    modifier: Modifier = Modifier,
    viewModel: FinanceChartCardViewModel,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    FinanceChartCardView(
        state = state.value,
        title = title,
        modifier = modifier
    )
}


/**
 * Finance chart card view
 *
 * @param title Title
 * @param state Finance chart card state
 * @param modifier Modifier
 */
@Composable
fun FinanceChartCardView(
    title: String,
    state: FinanceChartCardState,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        shape = RoundedCornerShape(dimensionResource(R.dimen.chart_round_corner_size_small)),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
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

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(vertical = 12.dp)
            )


            if (!state.dataAvailable) {
                Text(
                    text = "No data available",
                    style = MaterialTheme.typography.headlineMediumEmphasized,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .weight(0.8f)
                )
            } else {
                FinanceLineChart(
                    items = listOf(
                        LineChartData(
                            label = stringResource(R.string.finance_type_income),
                            color = Color(0xFF12C51A),
                            values = state.income,
                        ),
                        LineChartData(
                            label = stringResource(R.string.finance_type_expense),
                            color = Color(0xFFBE0E1F),
                            values = state.expense,
                        ),
                    ),
                    labels = lineLabels,
                    currencyCode = state.currencyCode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .weight(0.8f)
                )
            }
        }
    }
}

@Preview
@Composable
fun FinanceChartCardViewPreview() {
    MaterialTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) { innerPadding ->
            FinanceChartCardView(
                state = FinanceChartCardState(
                    income = listOf(10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0),
                    expense = listOf(5.0, 15.0, 25.0, 35.0, 45.0, 55.0, 65.0),
                    dataAvailable = true
                ),
                title = "Jan 2026",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .heightIn(max = 350.dp)
            )
        }
    }
}