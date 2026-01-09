package com.jd.financeflow.features.finance.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

import com.jd.financeflow.R
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.core.presentation.components.ErrorDialog
import com.jd.financeflow.features.finance.domain.models.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.IncomeCategory
import com.jd.financeflow.features.finance.presentation.home.components.BalanceCard
import com.jd.financeflow.features.finance.presentation.home.components.CategoryOverviewGrid
import com.jd.financeflow.ui.theme.FinanceFlowTheme
import com.jd.financeflow.ui.theme.primaryLightMediumContrast

/**
 * Screen for the home screen.
 *
 * @param modifier modifier to be applied
 * @param viewModel view model to be used
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    onAddFinanceClicked: () -> Unit = {}
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    HomeView(
        state = state.value,
        onAction = { action ->
            when (action) {
                is HomeActions.OnAddFinanceClicked -> onAddFinanceClicked()
                else -> viewModel.onAction(action)
            }
        },
        modifier = modifier,
    )
}

/**
 * View for the home screen.
 *
 * @param state state to be displayed
 * @param onAction action to be performed
 * @param modifier modifier to be applied
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    state: HomeState,
    onAction: (HomeActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Surface(
            color = primaryLightMediumContrast,
            shape = RoundedCornerShape(
                bottomStart = 32.dp,
                bottomEnd = 32.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
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
                        onClick = { onAction(HomeActions.OnAddFinanceClicked) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                        )
                    }
                }
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
            lineGraphMaxValue = state.income,
            currencyCode = state.currencyCode,
            lineGraphColor = Color.Green,
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
            lineGraphMaxValue = state.expense,
            currencyCode = state.currencyCode,
            lineGraphColor = Color.Red,
            onClicked = { },
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        )

        if (state.error != null) {
            ErrorDialog(
                content = state.error.asString(),
                onConfirmation = { onAction(HomeActions.OnClearError) },
                onDismiss = { onAction(HomeActions.OnClearError) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomeViewPreview() {
    val incomePreviewCategories = listOf(
        Pair(
            FinanceCategory.Income(IncomeCategory.Salary),
            100_000.0
        ),
        Pair(
            FinanceCategory.Income(IncomeCategory.Bonus),
            50_000.0
        ),
        Pair(
            FinanceCategory.Income(IncomeCategory.Investments),
            40_000.0
        ),
        Pair(
            FinanceCategory.Income(IncomeCategory.Commissions),
            10_000.0
        )
    )

    val expensePreviewCategories = listOf(
        Pair(
            FinanceCategory.Expense(ExpenseCategory.Housing),
            10_000.0
        ),
        Pair(
            FinanceCategory.Expense(ExpenseCategory.Utility),
            10_000.0
        ),
        Pair(
            FinanceCategory.Expense(ExpenseCategory.Transport),
            5_000.0
        ),
        Pair(
            FinanceCategory.Expense(ExpenseCategory.Entertainment),
            5_000.0
        )
    )

    FinanceFlowTheme {
        Scaffold { innerPadding ->
            HomeView(
                state = HomeState(
                    error = null, //DataError.Local.INVALID_ARGUMENT.toUiText(),
                    balance = 100_000.0,
                    income = 200_000.0,
                    expense = 30_000.0,
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
