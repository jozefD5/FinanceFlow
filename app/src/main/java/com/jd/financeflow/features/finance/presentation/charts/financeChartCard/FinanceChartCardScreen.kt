package com.jd.financeflow.features.finance.presentation.charts.financeChartCard

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.jd.financeflow.R
import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.core.presentation.components.formatCurrency
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.finance.domain.models.chart.LineChartData
import com.jd.financeflow.features.finance.domain.models.chart.PieChartData
import com.jd.financeflow.features.finance.presentation.charts.components.FinanceLineChart
import com.jd.financeflow.features.finance.presentation.charts.components.FinancePieChart


// Line chart labels
private val monthlyLineLabels = listOf("1", "15", "31")
private val annualLineLabels = listOf("1", "6", "12")

// Colors for Income
private val incomeChartColors = listOf(
    Color(0xFF2E7D32), // Forest Green
    Color(0xFF00BFA5), // Teal A700
    Color(0xFF00B0FF), // Light Blue A700
    Color(0xFF64DD17), // Light Green A700
    Color(0xFF00E5FF), // Cyan A400
    Color(0xFF1DE9B6), // Teal A400
    Color(0xFF76FF03), // Light Green A400
    Color(0xFF00E676)  // Green A400
)

// Colors for Expenses
private val expenseChartColors = listOf(
    Color(0xFFC62828), // Red 800
    Color(0xFFFF7043), // Deep Orange 400
    Color(0xFFAB47BC), // Purple 400
    Color(0xFFFFCA28), // Amber 400
    Color(0xFFEC407A), // Pink 400
    Color(0xFF7E57C2), // Deep Purple 400
    Color(0xFFFF5252), // Red A200
    Color(0xFFFFAB40)  // Orange A200
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
        title = title,
        state = state.value,
        interval = viewModel.interval,
        modifier = modifier
    )
}

/**
 * Finance chart card view
 *
 * @param title Title
 * @param state Finance chart card state
 * @param interval Interval of the chart
 * @param modifier Modifier
 * @param verticalScrollState Vertical scroll state
 */
@Composable
fun FinanceChartCardView(
    title: String,
    state: FinanceChartCardState,
    interval: FinanceInterval,
    modifier: Modifier = Modifier,
    verticalScrollState: ScrollState = rememberScrollState()
) {
    Column(modifier = modifier.verticalScroll(verticalScrollState)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (state.dataAvailable) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.finance_balance_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = formatCurrency(state.balance, state.currencyCode),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(Modifier.padding(8.dp))

        if (!state.dataAvailable) {
            Text(
                text = stringResource(R.string.no_data_available_label),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp)
            )
        } else {
            FinanceLineChart(
                items = listOf(
                    LineChartData(
                        label = stringResource(R.string.finance_type_income),
                        color = Color(0xFF4CAF50), // Standard MD Green
                        values = state.incomeTransactions,
                    ),
                    LineChartData(
                        label = stringResource(R.string.finance_type_expense),
                        color = Color(0xFFF44336), // Standard MD Red
                        values = state.expenseTransactions,
                    ),
                ),
                labels = if (interval == FinanceInterval.Monthly) monthlyLineLabels else annualLineLabels,
                currencyCode = state.currencyCode,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            Spacer(Modifier.height(24.dp))

            FinancePieChart(
                title = stringResource(R.string.finance_type_income),
                items = state.incomeCategories.mapIndexedNotNull { index, it ->
                    if (it.second > 0) {
                        PieChartData(
                            label = it.first.asString(),
                            color = incomeChartColors[index % incomeChartColors.size],
                            selectedColor = MaterialTheme.colorScheme.primaryContainer,
                            data = it.second,
                            selected = false
                        )
                    } else {
                        null
                    }
                },
                onItemClick = {},
                currencyCode = state.currencyCode,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            FinancePieChart(
                title = stringResource(R.string.finance_type_expense),
                items = state.expenseCategories.mapIndexedNotNull { index, it ->
                    if (it.second > 0) {
                        PieChartData(
                            label = it.first.asString(),
                            color = expenseChartColors[index % expenseChartColors.size],
                            selectedColor = MaterialTheme.colorScheme.errorContainer,
                            data = it.second,
                            selected = false
                        )
                    } else {
                        null
                    }
                },
                onItemClick = {},
                currencyCode = state.currencyCode,
                modifier = Modifier.fillMaxWidth()
            )
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
                title = "Jan 2026",
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
                interval = FinanceInterval.Monthly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            )
        }
    }
}