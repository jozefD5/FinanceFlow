package com.jd.financeflow.features.finance.presentation.charts

import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

import com.jd.financeflow.R
import com.jd.financeflow.features.finance.presentation.charts.financeChartCard.FinanceChartCard



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
        initialPage = viewModel.initialPage,
        totalMonths = viewModel.totalMonths,
        baseDate = viewModel.baseData,
        monthFormatter = viewModel.monthFormatter,
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
    initialPage: Int,
    totalMonths: Int,
    baseDate: LocalDate,
    monthFormatter: DateTimeFormatter,
    onAction: (ChartsActions) -> Unit,
    modifier: Modifier = Modifier,
    verticalScrollState: ScrollState = rememberScrollState()
) {
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
            val pagerState = rememberPagerState(
                initialPage = initialPage,
                pageCount = { totalMonths }
            )

            HorizontalPager(
                state = pagerState,
                key = { page -> page },
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                // Calculate the offset relative to our middle starting point.
                val monthOffset = page - initialPage
                val targetMonth = baseDate.plusMonths(monthOffset.toLong())

                // Convert LocalDate to Epoch Milliseconds (UTC)
                val epochMillis = targetMonth.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

                FinanceChartCard(
                    title = targetMonth.format(monthFormatter),
                    viewModel = koinViewModel(
                        key = targetMonth.toString(),
                        parameters = {
                            parametersOf(
                                epochMillis,
                                state.currencyCode
                            )
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                )
            }
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
                initialPage = 0,
                totalMonths = 120,
                baseDate = LocalDate.now(),
                monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy"),
                onAction = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}