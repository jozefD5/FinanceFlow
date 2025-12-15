package com.jd.financeflow.features.finance.presentation.home

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jd.financeflow.R
import org.koin.androidx.compose.koinViewModel

import com.jd.financeflow.features.core.domain.finance.FinanceCategory
import com.jd.financeflow.features.finance.presentation.home.components.BalanceCard
import com.jd.financeflow.features.finance.presentation.home.components.CategoryOverviewGrid
import com.jd.financeflow.ui.theme.FinanceFlowTheme

/**
 * Screen for the home screen.
 *
 * @param modifier modifier to be applied
 * @param viewModel view model to be used
 * @param addTransactionContent content for the add transaction screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    addTransactionContent: @Composable () -> Unit = {}
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    HomeView(
        state = state.value,
        onAction = { viewModel.onAction(it) },
        modifier = modifier,
        addTransactionContent = addTransactionContent
    )
}

/**
 * View for the home screen.
 *
 * @param state state to be displayed
 * @param onAction action to be performed
 * @param modifier modifier to be applied
 * @param addTransactionContent content for the add transaction screen
 * @param scope scope to be used
 * @param sheetState sheet state to be used
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    state: HomeState,
    onAction: (HomeActions) -> Unit,
    modifier: Modifier = Modifier,
    addTransactionContent: @Composable () -> Unit = {},
    scope: CoroutineScope = rememberCoroutineScope(),
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Surface(
            color = Color(0xFF1C2781),
            shape = RoundedCornerShape(
                bottomStart = 32.dp,
                bottomEnd = 32.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                BalanceCard(
                    balance = state.balance,
                    income = state.income,
                    expense = state.expense,
                    currencyCode = state.currencyCode,
                    color = Color.Transparent,
                    textColor = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    FilledTonalButton(
                        onClick = { },
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NorthEast,
                            contentDescription = null,
                        )
                    }

                    FilledTonalButton(
                        onClick = { },
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SouthWest,
                            contentDescription = null,
                        )
                    }

                    Button(
                        onClick = { onAction(HomeActions.ShowBottomSheet(true)) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Text(
            text = stringResource(R.string.income_overview_title),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        )

        CategoryOverviewGrid(
            categories = state.incomeCategoriesPreview,
            currencyCode = state.currencyCode,
            onClicked = { },
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        )

        Text(
            text = stringResource(R.string.expense_overview_title),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        )

        CategoryOverviewGrid(
            categories = state.expenseCategoriesPreview,
            currencyCode = state.currencyCode,
            onClicked = { },
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        )

        if (state.showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onAction(HomeActions.ShowBottomSheet(false))
                    }
                },
                sheetState = sheetState,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                addTransactionContent()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomeViewPreview() {
    val incomePreviewCategories = listOf(
        Pair(
            FinanceCategory.Income(FinanceCategory.IncomeCategory.Salary),
            100_000.0
        ),
        Pair(
            FinanceCategory.Income(FinanceCategory.IncomeCategory.Bonus),
            50_000.0
        ),
        Pair(
            FinanceCategory.Income(FinanceCategory.IncomeCategory.Investments),
            40_000.0
        ),
        Pair(
            FinanceCategory.Income(FinanceCategory.IncomeCategory.Commissions),
            30_000.0
        )
    )

    val expensePreviewCategories = listOf(
        Pair(
            FinanceCategory.Expense(FinanceCategory.ExpenseCategory.Housing),
            10_000.0
        ),
        Pair(
            FinanceCategory.Expense(FinanceCategory.ExpenseCategory.Utility),
            5_000.0
        ),
        Pair(
            FinanceCategory.Expense(FinanceCategory.ExpenseCategory.Transport),
            2_000.0
        ),
        Pair(
            FinanceCategory.Expense(FinanceCategory.ExpenseCategory.Entertainment),
            200.0
        )
    )

    FinanceFlowTheme {
        Scaffold { innerPadding ->
            HomeView(
                state = HomeState(
                    showBottomSheet = false,
                    balance = 100_000.0,
                    income = 150_000.0,
                    expense = 50_000.0,
                    incomeCategoriesPreview = incomePreviewCategories,
                    expenseCategoriesPreview = expensePreviewCategories
                ),
                onAction = {},
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
            )
        }
    }
}
