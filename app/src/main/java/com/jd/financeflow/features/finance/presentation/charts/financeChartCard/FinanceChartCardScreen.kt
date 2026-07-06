package com.jd.financeflow.features.finance.presentation.charts.financeChartCard

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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

private val monthlyLineLabels = listOf("1", "15", "31")
private val annualLineLabels = listOf("1", "6", "12")

private val incomeChartColors = listOf(
    Color(0xFF2E7D32),
    Color(0xFF00BFA5),
    Color(0xFF00B0FF),
    Color(0xFF64DD17),
    Color(0xFF00E5FF),
    Color(0xFF1DE9B6),
    Color(0xFF76FF03),
    Color(0xFF00E676),
)

private val expenseChartColors = listOf(
    Color(0xFFC62828),
    Color(0xFFFF7043),
    Color(0xFFAB47BC),
    Color(0xFFFFCA28),
    Color(0xFFEC407A),
    Color(0xFF7E57C2),
    Color(0xFFFF5252),
    Color(0xFFFFAB40),
)

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
        interval = viewModel.chartInterval,
        modifier = modifier,
    )
}

@Composable
fun FinanceChartCardView(
    title: String,
    state: FinanceChartCardState,
    interval: FinanceInterval,
    modifier: Modifier = Modifier,
    verticalScrollState: ScrollState = rememberScrollState(),
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.verticalScroll(verticalScrollState),
    ) {
        // ── Period header card ────────────────────────────────────────────
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                )
                if (state.dataAvailable) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(R.string.finance_balance_title),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.alpha(0.65f),
                        )
                        Text(
                            text = formatCurrency(state.balance, state.currencyCode),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                            ),
                        )
                    }
                }
            }
        }

        // ── No-data state ─────────────────────────────────────────────────
        if (!state.dataAvailable) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 64.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ShowChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(48.dp)
                            .alpha(0.4f),
                    )
                    Text(
                        text = stringResource(R.string.no_data_available_label),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            // ── Line chart ────────────────────────────────────────────────
            FinanceLineChart(
                items = listOf(
                    LineChartData(
                        label = stringResource(R.string.finance_type_income),
                        color = Color(0xFF4CAF50),
                        values = state.incomeTransactions,
                    ),
                    LineChartData(
                        label = stringResource(R.string.finance_type_expense),
                        color = Color(0xFFF44336),
                        values = state.expenseTransactions,
                    ),
                    LineChartData(
                        label = stringResource(R.string.finance_balance_title),
                        color = Color(0xFF2196F3),
                        values = state.balanceLevel,
                    ),
                ),
                labels = if (interval == FinanceInterval.Monthly) monthlyLineLabels else annualLineLabels,
                enableLabelHelperProperties = true,
                currencyCode = state.currencyCode,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
            )

            // ── Income pie chart ──────────────────────────────────────────
            FinancePieChart(
                title = stringResource(R.string.finance_type_income),
                items = state.incomeCategories.mapIndexedNotNull { index, it ->
                    if (it.second > 0) {
                        PieChartData(
                            label = it.first.asString(),
                            color = incomeChartColors[index % incomeChartColors.size],
                            selectedColor = MaterialTheme.colorScheme.primaryContainer,
                            data = it.second,
                            selected = false,
                        )
                    } else null
                },
                onItemClick = {},
                currencyCode = state.currencyCode,
                modifier = Modifier.fillMaxWidth(),
            )

            // ── Expense pie chart ─────────────────────────────────────────
            FinancePieChart(
                title = stringResource(R.string.finance_type_expense),
                items = state.expenseCategories.mapIndexedNotNull { index, it ->
                    if (it.second > 0) {
                        PieChartData(
                            label = it.first.asString(),
                            color = expenseChartColors[index % expenseChartColors.size],
                            selectedColor = MaterialTheme.colorScheme.errorContainer,
                            data = it.second,
                            selected = false,
                        )
                    } else null
                },
                onItemClick = {},
                currencyCode = state.currencyCode,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Preview
@Composable
private fun FinanceChartCardViewPreview() {
    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
                    ),
                ),
                interval = FinanceInterval.Monthly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(16.dp),
            )
        }
    }
}
