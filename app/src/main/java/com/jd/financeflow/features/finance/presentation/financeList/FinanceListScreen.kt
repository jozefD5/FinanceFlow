package com.jd.financeflow.features.finance.presentation.financeList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.dimensionResource
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
import com.jd.financeflow.features.finance.domain.models.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.finance.domain.models.IncomeCategory
import com.jd.financeflow.features.finance.presentation.components.financeList.FinanceList

/**
 * Screen for displaying a list of finance transactions.
 *
 * @param modifier modifier to be applied
 * @param onFinanceClicked callback for when an item is clicked
 * @param onFilterClicked callback for when the filter button is clicked
 * @param onAddFinanceClicked callback for when the add button is clicked
 * @param viewModel view model to be used
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceListScreen(
    modifier: Modifier = Modifier,
    onFinanceClicked: (id: Int) -> Unit = {},
    onFilterClicked: () -> Unit = {},
    onAddFinanceClicked: () -> Unit = {},
    viewModel: FinanceListViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

//    LifecycleResumeEffect(Unit) {
//        viewModel.onAction(FinanceListActions.OnRefresh)
//        onPauseOrDispose { }
//    }

    FinanceListView(
        state = state.value, onAction = { action ->
            when (action) {
                is FinanceListActions.OnFinanceItemClicked -> onFinanceClicked(action.id)
                is FinanceListActions.OnFilterClicked -> onFilterClicked()
                is FinanceListActions.OnAddFinanceClicked -> onAddFinanceClicked()
                else -> viewModel.onAction(action)
            }
        }, modifier = modifier
    )
}

/**
 * View for displaying a list of finance transactions.
 *
 * @param state state to be displayed
 * @param onAction action to be performed when item is clicked
 * @param modifier modifier to be applied
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FinanceListView(
    state: FinanceListState,
    onAction: (FinanceListActions) -> Unit,
    modifier: Modifier = Modifier,
    searchBarState: SearchBarState = rememberSearchBarState()
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.isSearching) {
        if (state.isSearching) {
            searchBarState.animateToExpanded()
            focusRequester.requestFocus()
        } else {
            searchBarState.animateToCollapsed()
        }
    }

    Box(
        modifier = modifier
    ) {
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onAction(FinanceListActions.OnRefresh) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formatCurrency(state.currentBalance, state.currencyCode),
                    style = MaterialTheme.typography.titleMediumEmphasized.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.padding_medium),
                    )
                )
                Text(
                    text = stringResource(R.string.filter_balance_label),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.padding_medium),
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = dimensionResource(R.dimen.padding_large),
                            horizontal = dimensionResource(R.dimen.padding_medium),
                        )
                ) {
                    SearchBar(
                        state = searchBarState, inputField = {
                            SearchBarDefaults.InputField(
                                query = state.searchQuery,
                                onQueryChange = {
                                    onAction(FinanceListActions.OnUpdateSearchQuery(it))
                                },
                                onSearch = {
                                    onAction(FinanceListActions.OnSearch)
                                },
                                leadingIcon = {
                                    if (state.isSearching) {
                                        IconButton(onClick = {
                                            onAction(
                                                FinanceListActions.OnEnableSearch(
                                                    false
                                                )
                                            )
                                        }) {
                                            Icon(
                                                Icons.AutoMirrored.Filled.ArrowBackIos,
                                                contentDescription = null
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
                                onExpandedChange = { toggle ->
                                    onAction(FinanceListActions.OnEnableSearch(toggle))
                                },
                                placeholder = { Text(stringResource(R.string.search_query_placeholder)) },
                                modifier = Modifier.focusRequester(focusRequester)
                            )
                        }, modifier = Modifier
                            .weight(1f)
                            .alpha(0.85f)
                    )

                    AnimatedVisibility(
                        visible = !state.isSearching,
                        enter = fadeIn() + expandHorizontally(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        OutlinedButton(
                            onClick = {
                                onAction(FinanceListActions.OnFilterClicked)
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(
                        topStart = dimensionResource(R.dimen.padding_extra_extra_large),
                        topEnd = dimensionResource(R.dimen.padding_extra_extra_large)
                    ), modifier = Modifier.fillMaxSize()
                ) {
                    FinanceList(
                        finances = state.finances,
                        currencyCode = state.currencyCode,
                        onItemClick = {
                            onAction(FinanceListActions.OnFinanceItemClicked(it.id))
                        },
                        onItemLongClick = {
                            onAction(
                                FinanceListActions.OnSelectedDeleteFinanceItem(
                                    showDialog = true, finance = it
                                )
                            )
                        },
                        contentPadding = PaddingValues(dimensionResource(R.dimen.padding_large)),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                onAction(FinanceListActions.OnAddFinanceClicked)
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        if (state.showDeleteDialog) {
            AlertDialog(
                title = { Text(text = stringResource(R.string.delete_finance_item_title)) },
                text = {
                    Column {
                        Text(
                            text = stringResource(R.string.delete_finance_item_text),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        state.selectedDeleteFinance?.title?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMediumEmphasized,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                onDismissRequest = {
                    onAction(
                        FinanceListActions.OnSelectedDeleteFinanceItem(
                            showDialog = false, finance = null
                        )
                    )
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = {
                            onAction(
                                FinanceListActions.OnSelectedDeleteFinanceItem(
                                    showDialog = false, finance = null
                                )
                            )
                        }) {
                        Text(stringResource(R.string.dismiss_title))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            state.selectedDeleteFinance?.let {
                                onAction(FinanceListActions.OnFinanceItemDelete(it))
                            }
                        }) {
                        Text(stringResource(R.string.confirm_title))
                    }
                })
        }

        if (state.error != null) {
            ErrorDialog(
                content = state.error.asString(),
                onConfirmation = { onAction(FinanceListActions.OnClearError) },
                onDismiss = { onAction(FinanceListActions.OnClearError) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun FinanceListViewPreview() {
    val currentUnixTime = System.currentTimeMillis()

    val finances = listOf(
        Finance(
            title = "Salary",
            description = "Monthly salary",
            amount = 10_000.0,
            timeStampMs = currentUnixTime,
            interval = FinanceInterval.Monthly,
            category = FinanceCategory.Income(IncomeCategory.Salary),
        ), Finance(
            title = "Bonus",
            description = "Work bonus",
            amount = 5_000.0,
            timeStampMs = currentUnixTime,
            interval = FinanceInterval.Quarterly,
            category = FinanceCategory.Income(IncomeCategory.Bonus)
        ), Finance(
            title = "Investment",
            description = "",
            amount = 1000.0,
            timeStampMs = currentUnixTime,
            interval = FinanceInterval.Quarterly,
            category = FinanceCategory.Income(IncomeCategory.Investment)
        ), Finance(
            title = "Housing",
            description = "Rent",
            amount = 500.0,
            timeStampMs = currentUnixTime,
            interval = FinanceInterval.Monthly,
            category = FinanceCategory.Expense(ExpenseCategory.Housing)
        )
    )

    MaterialTheme {
        Scaffold { innerPadding ->
            FinanceListView(
                state = FinanceListState(
                    isLoading = true,
                    currencyCode = "EUR",
                    finances = finances,
                    searchQuery = "abc123",
                    //error = DataError.Local.UNKNOWN.toUiText()
                ), onAction = {}, modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
