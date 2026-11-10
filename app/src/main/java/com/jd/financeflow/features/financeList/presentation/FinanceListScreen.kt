package com.jd.financeflow.features.financeList.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import org.koin.androidx.compose.koinViewModel

import com.jd.financeflow.R
import com.jd.financeflow.features.core.presentation.components.ErrorDialog
import com.jd.financeflow.features.core.presentation.components.formatCurrency
import com.jd.financeflow.features.core.domain.finance.ExpenseCategory
import com.jd.financeflow.features.core.domain.finance.Finance
import com.jd.financeflow.features.core.domain.finance.FinanceCategory
import com.jd.financeflow.features.core.domain.finance.FinanceInterval
import com.jd.financeflow.features.core.domain.finance.IncomeCategory
import com.jd.financeflow.features.finance.presentation.components.financeList.FinanceList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceListScreen(
    modifier: Modifier = Modifier,
    onFinanceClicked: (id: Int) -> Unit = {},
    onFilterClicked: () -> Unit = {},
    onAddFinanceClicked: () -> Unit = {},
    viewModel: FinanceListViewModel = koinViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.onAction(FinanceListActions.OnRefresh)
        onPauseOrDispose { }
    }

    FinanceListView(
        state = state.value,
        onAction = { action ->
            when (action) {
                is FinanceListActions.OnFinanceItemClicked -> onFinanceClicked(action.id)
                is FinanceListActions.OnFilterClicked -> onFilterClicked()
                is FinanceListActions.OnAddFinanceClicked -> onAddFinanceClicked()
                else -> viewModel.onAction(action)
            }
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FinanceListView(
    state: FinanceListState,
    onAction: (FinanceListActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onAction(FinanceListActions.OnRefresh) },
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Balance summary — hidden while searching to give search full focus
                AnimatedVisibility(
                    visible = !state.isSearching,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    BalanceSummaryCard(
                        balance = state.currentBalance,
                        currencyCode = state.currencyCode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }

                // Search bar + filter button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    DockedSearchBar(
                        inputField = {
                            SearchBarDefaults.InputField(
                                query = state.searchQuery,
                                onQueryChange = { onAction(FinanceListActions.OnUpdateSearchQuery(it)) },
                                onSearch = {},
                                leadingIcon = {
                                    if (state.isSearching) {
                                        IconButton(onClick = {
                                            onAction(FinanceListActions.OnEnableSearch(false))
                                        }) {
                                            Icon(
                                                Icons.AutoMirrored.Filled.ArrowBackIos,
                                                contentDescription = null,
                                            )
                                        }
                                    } else {
                                        Icon(Icons.Default.Search, contentDescription = null)
                                    }
                                },
                                trailingIcon = {
                                    if (state.searchQuery.isNotBlank()) {
                                        IconButton(onClick = {
                                            onAction(FinanceListActions.OnUpdateSearchQuery(""))
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = null,
                                            )
                                        }
                                    }
                                },
                                expanded = state.isSearching,
                                onExpandedChange = {
                                    onAction(FinanceListActions.OnEnableSearch(it))
                                },
                                placeholder = { Text(stringResource(R.string.search_query_placeholder)) },
                            )
                        },
                        expanded = false,
                        onExpandedChange = {},
                        content = {},
                        modifier = Modifier.weight(1f),
                    )

                    AnimatedVisibility(
                        visible = !state.isSearching,
                        enter = fadeIn() + expandHorizontally(),
                        exit = fadeOut() + shrinkHorizontally(),
                    ) {
                        OutlinedButton(
                            onClick = { onAction(FinanceListActions.OnFilterClicked) },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.height(SearchBarDefaults.InputFieldHeight),
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                }

                FinanceList(
                    finances = state.finances,
                    currencyCode = state.currencyCode,
                    onItemClick = { onAction(FinanceListActions.OnFinanceItemClicked(it.id)) },
                    onItemLongClick = {
                        onAction(
                            FinanceListActions.OnSelectedDeleteFinanceItem(
                                showDialog = true,
                                finance = it,
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }
        }

        FloatingActionButton(
            onClick = { onAction(FinanceListActions.OnAddFinanceClicked) },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
            )
        }

        if (state.showDeleteDialog) {
            AlertDialog(
                title = { Text(text = stringResource(R.string.delete_finance_item_title)) },
                text = {
                    Column {
                        Text(
                            text = stringResource(R.string.delete_finance_item_text),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        state.selectedDeleteFinance?.title?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMediumEmphasized,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                },
                onDismissRequest = {
                    onAction(
                        FinanceListActions.OnSelectedDeleteFinanceItem(
                            showDialog = false,
                            finance = null,
                        )
                    )
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = {
                            onAction(
                                FinanceListActions.OnSelectedDeleteFinanceItem(
                                    showDialog = false,
                                    finance = null,
                                )
                            )
                        },
                    ) {
                        Text(stringResource(R.string.dismiss_title))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            state.selectedDeleteFinance?.let {
                                onAction(FinanceListActions.OnFinanceItemDelete(it))
                            }
                        },
                    ) {
                        Text(stringResource(R.string.confirm_title))
                    }
                },
            )
        }

        if (state.error != null) {
            ErrorDialog(
                content = state.error.asString(),
                onConfirmation = { onAction(FinanceListActions.OnClearError) },
                onDismiss = { onAction(FinanceListActions.OnClearError) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BalanceSummaryCard(
    balance: Double,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Column {
                Text(
                    text = stringResource(R.string.finance_balance_label),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.alpha(0.65f),
                )
                Text(
                    text = formatCurrency(balance, currencyCode),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun FinanceListViewPreview() {
    val currentUnixTime = System.currentTimeMillis()
    val finances = listOf(
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
            title = "Housing",
            description = "Rent",
            amount = 500.0,
            timeStampMs = currentUnixTime,
            interval = FinanceInterval.Monthly,
            category = FinanceCategory.Expense(ExpenseCategory.Housing),
        ),
    )

    MaterialTheme {
        Scaffold { innerPadding ->
            FinanceListView(
                state = FinanceListState(
                    isLoading = false,
                    currencyCode = "EUR",
                    currentBalance = 14_500.0,
                    finances = finances,
                ),
                onAction = {},
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
