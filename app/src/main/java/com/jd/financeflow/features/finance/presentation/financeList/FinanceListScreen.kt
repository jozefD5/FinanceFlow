package com.jd.financeflow.features.finance.presentation.financeList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jd.financeflow.R
import org.koin.androidx.compose.koinViewModel

import com.jd.financeflow.features.core.presentation.components.ErrorDialog
import com.jd.financeflow.features.finance.domain.models.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.finance.domain.models.IncomeCategory
import com.jd.financeflow.features.finance.presentation.financeList.components.FinanceList
import com.jd.financeflow.ui.theme.primaryLightMediumContrast

/**
 * Screen for displaying a list of finance transactions.
 *
 * @param modifier modifier to be applied
 * @param onFinanceClicked callback for when an item is clicked
 * @param viewModel view model to be used
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceListScreen(
    modifier: Modifier = Modifier,
    onFinanceClicked: (id: Int) -> Unit = {},
    viewModel: FinanceListViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    FinanceListView(
        state = state.value,
        onAction = { action ->
            when (action) {
                is FinanceListActions.OnFinanceItemClicked -> onFinanceClicked(action.id)

                else -> viewModel.onAction(action)
            }
        },
        modifier = modifier
    )
}

/**
 * View for displaying a list of finance transactions.
 *
 * @param state state to be displayed
 * @param onAction action to be performed when item is clicked
 * @param modifier modifier to be applied
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceListView(
    state: FinanceListState,
    onAction: (FinanceListActions) -> Unit,
    modifier: Modifier = Modifier,
    searchBarState: SearchBarState = rememberSearchBarState()
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .background(primaryLightMediumContrast)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    bottom = 16.dp,
                    start = 8.dp,
                    end = 8.dp
                )
        ) {
            SearchBar(
                state = searchBarState,
                inputField = {
                    SearchBarDefaults.InputField(
                        query = state.searchQuery,
                        onQueryChange = {
                            onAction(FinanceListActions.OnUpdateSearchQuery(it))
                        },
                        onSearch = {
                            onAction(FinanceListActions.OnSearch())
                        },
                        expanded = false,
                        onExpandedChange = { },
                        placeholder = { Text("Search") }
                    )
                },
                modifier = Modifier
                    .weight(1f)
            )

            IconButton(
                onClick = { },
                modifier = Modifier.weight(0.15f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = null,
                    tint = Color(0xFFFFFFFF)
                )
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(
                topStart = 32.dp,
                topEnd = 32.dp
            ),
            modifier = Modifier
                .fillMaxSize()
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
                            shoeDialog = true,
                            finance = it
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }

    if (state.showDeleteDialog) {
        AlertDialog(
            icon = {},
            title = { Text(text = stringResource(R.string.delete_finance_item_title)) },
            text = {
                Text(
                    """
                        ${stringResource(R.string.delete_finance_item_text)}
                        ${state.selectedDeleteFinance?.title}
                    """.trimIndent()
                )
            },
            onDismissRequest = {
                onAction(
                    FinanceListActions.OnSelectedDeleteFinanceItem(
                        shoeDialog = false,
                        finance = null
                    )
                )
            },
            dismissButton = {
                Button(
                    onClick = {
                        onAction(
                            FinanceListActions.OnSelectedDeleteFinanceItem(
                                shoeDialog = false,
                                finance = null
                            )
                        )
                    }
                ) {
                    Text(stringResource(R.string.dismiss_title))
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        state.selectedDeleteFinance?.let {
                            onAction(FinanceListActions.OnFinanceItemDelete(it))
                        }
                    }
                ) {
                    Text(stringResource(R.string.confirm_title))
                }
            }
        )
    }

    if (state.error != null) {
        ErrorDialog(
            content = state.error.asString(),
            onConfirmation = { onAction(FinanceListActions.OnClearError()) },
            onDismiss = { onAction(FinanceListActions.OnClearError()) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun FinanceListViewPreview() {
    val finances = listOf(
        Finance(
            title = "Salary",
            description = "Monthly salary",
            amount = 10_000.0,
            interval = FinanceInterval.Monthly,
            category = FinanceCategory.Income(IncomeCategory.Salary),
        ),
        Finance(
            title = "Bonus",
            description = "Work bonus",
            amount = 5_000.0,
            interval = FinanceInterval.Quarterly,
            category = FinanceCategory.Income(IncomeCategory.Bonus)
        ),
        Finance(
            title = "Investment",
            description = "",
            amount = 1000.0,
            interval = FinanceInterval.Quarterly,
            category = FinanceCategory.Income(IncomeCategory.Investments)
        ),
        Finance(
            title = "Housing",
            description = "Rent",
            amount = 500.0,
            interval = FinanceInterval.Monthly,
            category = FinanceCategory.Expense(ExpenseCategory.Housing)
        )
    )

    MaterialTheme {
        Scaffold { innerPadding ->
            FinanceListView(
                state = FinanceListState(
                    finances = finances
                ),
                onAction = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
