package com.jd.financeflow.features.finance.presentation.charts

import androidx.compose.foundation.layout.Column
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

import com.jd.financeflow.R
import com.jd.financeflow.features.finance.presentation.charts.financeChartCard.FinanceChartCard


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
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.chart_padding_medium))
    ) {
        val pagerState = rememberPagerState(
            initialPage = initialPage,
            pageCount = { totalMonths }
        )

        HorizontalPager(
            state = pagerState,
            key = { page -> page },
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // Calculate the offset relative to our middle starting point.
            val monthOffset = page - initialPage
            val targetMonth = baseDate.plusMonths(monthOffset.toLong())

            // Convert LocalDate to Epoch Milliseconds (UTC)
            val epochMillis =
                targetMonth.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

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
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.padding_medium))
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