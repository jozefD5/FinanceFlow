package com.jd.financeflow.features.finance.presentation.charts

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.ZoneOffset

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

import com.jd.financeflow.R
import com.jd.financeflow.features.core.domain.finance.FinanceInterval
import com.jd.financeflow.features.finance.presentation.charts.financeChartCard.FinanceChartCard

@Composable
fun ChartsScreen(
    modifier: Modifier = Modifier,
    viewModel: ChartsViewModel = koinViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    ChartsView(
        state = state.value,
        initialPage = viewModel.initialPage,
        totalPages = viewModel.totalPages,
        baseDate = viewModel.baseData,
        monthFormatter = viewModel.monthFormatter,
        yearFormatter = viewModel.yearFormatter,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChartsView(
    state: ChartsState,
    initialPage: Int,
    totalPages: Int,
    baseDate: LocalDate,
    monthFormatter: DateTimeFormatter,
    yearFormatter: DateTimeFormatter,
    onAction: (ChartsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val intervalOptions = listOf("Monthly", "Annual")

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        // Header
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Text(
                text = stringResource(R.string.screen_title_charts),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = stringResource(R.string.charts_subtitle),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.alpha(0.6f),
            )
        }

        // Interval selector
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.chart_padding_medium), vertical = 4.dp),
        ) {
            intervalOptions.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = intervalOptions.size,
                    ),
                    selected = state.selectedIntervalIndex == index,
                    onClick = { onAction(ChartsActions.OnTabSelected(index)) },
                    label = { Text(label) },
                )
            }
        }

        // Pager
        when (state.selectedIntervalIndex) {
            0 -> {
                val monthlyPagerState = rememberPagerState(
                    initialPage = initialPage,
                    pageCount = { totalPages },
                )
                HorizontalPager(
                    state = monthlyPagerState,
                    key = { page -> page },
                    pageSpacing = 2.dp,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    val monthOffset = page - initialPage
                    val targetMonth = baseDate.plusMonths(monthOffset.toLong())
                    val epochMillis =
                        targetMonth.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

                    FinanceChartCard(
                        title = targetMonth.format(monthFormatter),
                        viewModel = koinViewModel(
                            key = "monthly_${targetMonth}",
                            parameters = {
                                parametersOf(
                                    epochMillis,
                                    FinanceInterval.Monthly,
                                    state.currencyCode,
                                )
                            },
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensionResource(R.dimen.padding_medium)),
                    )
                }
            }

            1 -> {
                val annualPagerState = rememberPagerState(
                    initialPage = initialPage,
                    pageCount = { totalPages },
                )
                HorizontalPager(
                    state = annualPagerState,
                    key = { page -> page },
                    pageSpacing = 2.dp,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    val yearOffset = page - initialPage
                    val targetYear = baseDate.plusYears(yearOffset.toLong()).withDayOfYear(1)
                    val epochMillis =
                        targetYear.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

                    FinanceChartCard(
                        title = targetYear.format(yearFormatter),
                        viewModel = koinViewModel(
                            key = "annual_${targetYear.year}",
                            parameters = {
                                parametersOf(
                                    epochMillis,
                                    FinanceInterval.Annually,
                                    state.currencyCode,
                                )
                            },
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensionResource(R.dimen.padding_medium)),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChartsViewPreview() {
    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            ChartsView(
                state = ChartsState(),
                initialPage = 0,
                totalPages = 120,
                baseDate = LocalDate.now(),
                monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy"),
                yearFormatter = DateTimeFormatter.ofPattern("yyyy"),
                onAction = {},
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
