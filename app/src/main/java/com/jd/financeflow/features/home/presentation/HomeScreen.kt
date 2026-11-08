package com.jd.financeflow.features.home.presentation

import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

import com.jd.financeflow.R
import com.jd.financeflow.features.core.presentation.components.ErrorDialog
import com.jd.financeflow.features.core.presentation.components.MainGradientBox
import com.jd.financeflow.features.core.domain.finance.ExpenseCategory
import com.jd.financeflow.features.core.domain.finance.Finance
import com.jd.financeflow.features.core.domain.finance.FinanceCategory
import com.jd.financeflow.features.core.domain.finance.FinanceInterval
import com.jd.financeflow.features.core.domain.finance.IncomeCategory
import com.jd.financeflow.features.finance.presentation.components.financeList.FinanceListItem
import com.jd.financeflow.features.home.presentation.components.BalanceCard
import com.jd.financeflow.ui.theme.FinanceFlowTheme

/**
 * Composable function for the Home screen, providing state collection and view rendering.
 *
 * @param modifier The modifier for this composable.
 * @param viewModel The view model for the home screen.
 * @param onAddFinanceClicked Callback invoked when add finance is clicked.
 * @param onAllActivityClicked Callback invoked when see all activity is clicked.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    onAddFinanceClicked: () -> Unit = {},
    onAllActivityClicked: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeView(
        state = state,
        onAction = { action ->
            when (action) {
                is HomeActions.OnAddFinanceClicked -> onAddFinanceClicked()
                is HomeActions.OnAllActivityClicked -> onAllActivityClicked()
                else -> viewModel.onAction(action)
            }
        },
        modifier = modifier,
    )
}

/**
 * Stateless view for the Home screen.
 *
 * @param state The current UI state ([HomeState]).
 * @param onAction Callback to handle user actions ([HomeActions]).
 * @param modifier The modifier for this composable.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeView(
    state: HomeState,
    onAction: (HomeActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val isFabExpanded by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex == 0 }
    }

    Box(modifier = modifier) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                HeroSection(
                    state = state,
                    onRefreshClicked = { onAction(HomeActions.OnRefreshBalance) },
                )
            }

            item {
                RecentActivityHeader(
                    onSeeAllClicked = { onAction(HomeActions.OnAllActivityClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }

            if (state.recentTransactions.isEmpty()) {
                item {
                    NoRecentTransactions(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                    )
                }
            } else {
                items(
                    items = state.recentTransactions,
                    key = { it.id },
                ) { finance ->
                    FinanceListItem(
                        finance = finance,
                        currencyCode = state.currencyCode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                    )
                }
            }

            // Space for the FAB
            item { Spacer(Modifier.height(88.dp)) }
        }

        ExtendedFloatingActionButton(
            onClick = { onAction(HomeActions.OnAddFinanceClicked) },
            expanded = isFabExpanded,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                )
            },
            text = { Text(stringResource(R.string.add_finance_button_label)) },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        )

        if (state.error != null) {
            ErrorDialog(
                content = state.error.asString(),
                onConfirmation = { onAction(HomeActions.OnClearError) },
                onDismiss = { onAction(HomeActions.OnClearError) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HeroSection(
    state: HomeState,
    onRefreshClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val formattedDate = remember {
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMM d")
        LocalDate.now().format(formatter).replaceFirstChar { it.uppercase() }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)),
    ) {
        MainGradientBox(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.home_tab_label),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color.White.copy(alpha = 0.70f),
                            ),
                        )
                    }
                    IconButton(onClick = onRefreshClicked) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f),
                        )
                    }
                }

                BalanceCard(
                    balance = state.balance,
                    income = state.income,
                    expense = state.expense,
                    currencyCode = state.currencyCode,
                    color = Color.Transparent,
                    textColor = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun RecentActivityHeader(
    onSeeAllClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.recent_activity_label),
            style = MaterialTheme.typography.titleMediumEmphasized.copy(
                fontWeight = FontWeight.Bold,
            ),
        )
        TextButton(onClick = onSeeAllClicked) {
            Text(
                text = stringResource(R.string.see_all_transactions_label),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                ),
            )
            Spacer(Modifier.width(2.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(12.dp),
            )
        }
    }
}

@Composable
private fun NoRecentTransactions(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Outlined.AccountBalanceWallet,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(52.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.no_recent_transactions_label),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeViewPreview() {
    val currentUnixTime = System.currentTimeMillis()
    val recentTransactions = listOf(
        Finance(
            title = "Salary",
            description = "Monthly salary",
            amount = 10_000.0,
            timeStampMs = currentUnixTime,
            interval = FinanceInterval.Monthly,
            category = FinanceCategory.Income(IncomeCategory.Salary),
        ),
        Finance(
            title = "Bonus",
            description = "Work bonus",
            amount = 5_000.0,
            timeStampMs = currentUnixTime,
            interval = FinanceInterval.Quarterly,
            category = FinanceCategory.Income(IncomeCategory.Bonus),
        ),
        Finance(
            title = "Investment",
            description = "",
            amount = 1_000.0,
            timeStampMs = currentUnixTime,
            interval = FinanceInterval.Quarterly,
            category = FinanceCategory.Income(IncomeCategory.Investment),
        ),
        Finance(
            title = "Housing",
            description = "Rent",
            amount = 500.0,
            timeStampMs = currentUnixTime,
            interval = FinanceInterval.Monthly,
            category = FinanceCategory.Expense(ExpenseCategory.Housing),
        ),
    )

    FinanceFlowTheme {
        Scaffold { innerPadding ->
            HomeView(
                state = HomeState(
                    balance = 100_000.0,
                    income = 200_000.0,
                    expense = 30_000.0,
                    recentTransactions = recentTransactions,
                ),
                onAction = {},
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
            )
        }
    }
}
