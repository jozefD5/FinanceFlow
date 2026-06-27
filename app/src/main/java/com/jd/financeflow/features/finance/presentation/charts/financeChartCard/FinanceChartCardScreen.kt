package com.jd.financeflow.features.finance.presentation.charts.financeChartCard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.core.presentation.components.formatCurrency
import com.jd.financeflow.features.finance.domain.models.chart.LineChartData
import com.jd.financeflow.features.finance.domain.models.chart.PieChartData
import com.jd.financeflow.features.finance.presentation.charts.components.FinanceLineChart
import com.jd.financeflow.features.finance.presentation.charts.components.FinancePieChart


// Monthly finance line chart labels
private val lineLabels = listOf("1", "6", "11", "16", "21", "26", "31")

// High-contrast colors for Income (Alternating distinct hues: Green, Blue, Lime, Teal)
private val incomeChartColors = listOf(
    Color(0xFF2E7D32), // Forest Green
    Color(0xFF0288D1), // Light Blue
    Color(0xFF9E9D24), // Olive/Lime
    Color(0xFF00897B), // Teal
    Color(0xFF1565C0), // Denim Blue
    Color(0xFF558B2F), // Light Green
    Color(0xFF00ACC1), // Cyan
    Color(0xFF004D40)  // Deep Pine
)

// High-contrast colors for Expenses (Alternating distinct hues: Red, Purple, Orange, Pink)
private val expenseChartColors = listOf(
    Color(0xFFC62828), // Red
    Color(0xFF6A1B9A), // Purple
    Color(0xFFEF6C00), // Orange
    Color(0xFFAD1457), // Pink/Magenta
    Color(0xFF4527A0), // Deep Indigo
    Color(0xFFD84315), // Deep Orange/Rust
    Color(0xFF283593), // Indigo
    Color(0xFF880E4F)  // Maroon
)

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
    verticalScrollState: ScrollState = rememberScrollState()
) {
    OutlinedCard(
        shape = RoundedCornerShape(dimensionResource(R.dimen.chart_round_corner_size_small)),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.chart_padding_medium))
                .verticalScroll(verticalScrollState)
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

            if (state.dataAvailable) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.finance_balance_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = formatCurrency(state.balance, state.currencyCode),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(Modifier.padding(4.dp))

            if (!state.dataAvailable) {
                Text(
                    text = stringResource(R.string.no_data_available_label),
                    style = MaterialTheme.typography.headlineMediumEmphasized,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .weight(0.7f)
                )
            } else {
                FinanceLineChart(
                    items = listOf(
                        LineChartData(
                            label = stringResource(R.string.finance_type_income),
                            color = Color(0xFF12C51A),
                            values = state.incomeTransactions,
                        ),
                        LineChartData(
                            label = stringResource(R.string.finance_type_expense),
                            color = Color(0xFFBE0E1F),
                            values = state.expenseTransactions,
                        ),
                    ),
                    labels = lineLabels,
                    currencyCode = state.currencyCode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .heightIn(max = 200.dp)
                )
            }

            if (state.dataAvailable) {
                FinancePieChart(
                    title = stringResource(R.string.finance_type_income),
                    items = state.incomeCategories.mapIndexed { index, it ->
                        PieChartData(
                            label = it.first.asString(),
                            color = incomeChartColors[index % incomeChartColors.size],
                            selectedColor = MaterialTheme.colorScheme.inversePrimary,
                            data = it.second,
                            selected = false
                        )
                    },
                    onItemClick = {},
                    currencyCode = state.currencyCode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .heightIn(max = 250.dp)
                )

                FinancePieChart(
                    title = stringResource(R.string.finance_type_expense),
                    items = state.expenseCategories.mapIndexed { index, it ->
                        PieChartData(
                            label = it.first.asString(),
                            color = expenseChartColors[index % expenseChartColors.size],
                            selectedColor = MaterialTheme.colorScheme.inversePrimary,
                            data = it.second,
                            selected = false
                        )
                    },
                    onItemClick = {},
                    currencyCode = state.currencyCode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .heightIn(max = 250.dp)
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
                    balance = 100.0,
                    dataAvailable = true,
                    incomeTransactions = listOf(10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0),
                    expenseTransactions = listOf(5.0, 15.0, 25.0, 35.0, 45.0, 55.0, 65.0),
                    incomeCategories = listOf(
                        Pair(UiText.DynamicString("Salary"), 500.0),
                        Pair(UiText.DynamicString("Investment"), 250.0),
                        Pair(UiText.DynamicString("Freelance"), 100.0),
                        Pair(UiText.DynamicString("Other"), 100.0),
                    )
                ),
                title = "Jan 2026",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            )
        }
    }
}