package com.jd.financeflow.features.finance.presentation.charts

import androidx.compose.foundation.BorderStroke
import java.time.Month

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
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
import com.jd.financeflow.features.finance.domain.models.chart.PieChartData
import com.jd.financeflow.features.finance.presentation.charts.components.ChartLegendItem
import com.jd.financeflow.features.finance.presentation.charts.components.ChartLegendItemGrid
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
 * Finance charts screen
 *
 * @param modifier Modifier
 * @param viewModel View model
 */
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

/**
 * Finance charts view
 *
 * @param state View state
 * @param onAction Action handler
 * @param modifier Modifier
 * @param verticalScrollState Vertical scroll state
 */
@Composable
fun ChartsView(
    state: ChartsState,
    onAction: (ChartsActions) -> Unit,
    modifier: Modifier = Modifier,
    verticalScrollState: ScrollState = rememberScrollState()
) {
    val options = listOf(
        stringResource(R.string.finance_type_income),
        stringResource(R.string.finance_type_expense),
        stringResource(R.string.finance_balance_title)
    )
    
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
            MultiChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        checked = state.selectedChartOptions[index],
                        onCheckedChange = {
                            onAction(ChartsActions.OnToggleChartOption(index))
                        },
                        icon = { SegmentedButtonDefaults.Icon(state.selectedChartOptions[index]) },
                        label = { Text(text = label) },
                    )
                }
            }

            OutlinedCard(
                shape = RoundedCornerShape(
                    dimensionResource(R.dimen.chart_round_corner_size_small)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                ),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                ChartLegendItemGrid(
                    data = listOf(
                        ChartLegendItem(
                            label = stringResource(R.string.finance_type_income),
                            color = Color(0xFF12C51A)
                        ),
                        ChartLegendItem(
                            label = stringResource(R.string.finance_type_expense),
                            color = Color(0xFFBE0E1F)
                        ),
                        ChartLegendItem(
                            label = stringResource(R.string.finance_balance_title),
                            color = Color(0xFF0C6EE5)
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.chart_padding_medium))
                )
            }

            FinanceLineChart(
                title = stringResource(R.string.chart_annual_cash_flow_title),
                items = listOf(
                    LineChartData(
                        label = stringResource(R.string.finance_type_income),
                        color = Color(0xFF12C51A),
                        values = state.yearlyCashFlowIncome,
                        enable = state.selectedChartOptions[0]
                    ),
                    LineChartData(
                        label = stringResource(R.string.finance_type_expense),
                        color = Color(0xFFBE0E1F),
                        values = state.yearlyCashFlowExpense,
                        enable = state.selectedChartOptions[1]
                    ),
                    LineChartData(
                        label = stringResource(R.string.finance_balance_title),
                        color = Color(0xFF0C6EE5),
                        values = state.yearlyCashFlowBalance,
                        enable = state.selectedChartOptions[2]
                    )
                ),
                labels = Month.entries.map {
                    it.name.substring(0, 3)
                        .lowercase(LocalLocale.current.platformLocale)
                        .replaceFirstChar { char -> char.uppercase() }
                },
                currencyCode = state.currencyCode,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp)
            )

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
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp)
            )

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
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
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