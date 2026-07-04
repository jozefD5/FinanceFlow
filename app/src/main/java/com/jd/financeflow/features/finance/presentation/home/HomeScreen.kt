package com.jd.financeflow.features.finance.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

import com.jd.financeflow.R
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.core.presentation.components.ErrorDialog
import com.jd.financeflow.features.core.presentation.components.MainGradientBox
import com.jd.financeflow.features.finance.domain.models.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.finance.domain.models.IncomeCategory
import com.jd.financeflow.features.finance.presentation.components.financeList.FinanceList
import com.jd.financeflow.features.finance.presentation.home.components.BalanceCard
import com.jd.financeflow.ui.theme.FinanceFlowTheme
import com.jd.financeflow.ui.theme.vibrantMediumBlue

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
    Box(
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
            modifier = Modifier.fillMaxSize()
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = dimensionResource(R.dimen.padding_extra_extra_large),
                    topEnd = dimensionResource(R.dimen.padding_extra_extra_large),
                    bottomStart = dimensionResource(R.dimen.padding_extra_extra_large),
                    bottomEnd = dimensionResource(R.dimen.padding_extra_extra_large)
                ),
                tonalElevation = 4.dp,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                MainGradientBox(
                    modifier = Modifier.fillMaxWidth()
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
                            .padding(16.dp)
                    )
                }
            }

            Text(
                text = stringResource(R.string.recent_transactions_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.padding_medium))
                    .fillMaxWidth()
            )

            FinanceList(
                finances = state.recentTransactions,
                currencyCode = state.currencyCode,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.padding_medium))
            )
        }

        FloatingActionButton(
            onClick = {
                onAction(HomeActions.OnAddFinanceClicked)
            },
            containerColor = vibrantMediumBlue,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = Color.White
            )
        }

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
    val currentUnixTime = System.currentTimeMillis()
    val recentTransactions = listOf(
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

    FinanceFlowTheme {
        Scaffold { innerPadding ->
            HomeView(
                state = HomeState(
                    error = null, //DataError.Local.INVALID_ARGUMENT.toUiText(),
                    balance = 100_000.0,
                    income = 200_000.0,
                    expense = 30_000.0,
                    recentTransactions = recentTransactions,
                ),
                onAction = {},
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
            )
        }
    }
}
