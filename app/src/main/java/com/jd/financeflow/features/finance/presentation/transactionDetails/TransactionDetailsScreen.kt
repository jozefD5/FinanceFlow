package com.jd.financeflow.features.finance.presentation.transactionDetails

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

import com.jd.financeflow.R
import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.core.presentation.components.LargeDropdownMenu
import com.jd.financeflow.features.finance.presentation.components.toUiText
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.finance.domain.models.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.IncomeCategory
import com.jd.financeflow.features.finance.domain.models.TransactionType

/**
 * Screen for adding or modifying finance transaction.
 *
 * @param modifier modifier to be applied
 * @param viewModel view model to be used
 */
@Composable
fun TransactionDetailsScreen(
    modifier: Modifier = Modifier,
    onSuccessfulAdd: () -> Unit = {},
    viewModel: TransactionDetailsViewModel = koinViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    TransactionDetailsView(
        state = state.value,
        onAction = { action ->
            val actualAction = when (action) {
                is TransactionDetailsActions.SaveTransaction -> action
                    .copy(action = onSuccessfulAdd)

                else -> action
            }

            viewModel.onAction(actualAction)
        },
        modifier = modifier
    )
}

/**
 * View for adding or modifying finance transaction.
 *
 * @param state state to be displayed
 * @param onAction action to be performed
 * @param modifier modifier to be applied
 * @param focusManager focus manager to be used
 * @param scrollState scroll state to be used
 */
@Composable
fun TransactionDetailsView(
    state: TransactionDetailsState,
    onAction: (TransactionDetailsActions) -> Unit,
    modifier: Modifier = Modifier,
    focusManager: FocusManager = LocalFocusManager.current,
    scrollState: ScrollState = rememberScrollState()
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (state.error != null) {
            Text(
                text = state.error.asString(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                TransactionType.entries.forEachIndexed { index, value ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = TransactionType.entries.size
                        ),
                        selected = state.financeTypeIndex == index,
                        onClick = { onAction(TransactionDetailsActions.UpdateFinanceType(index)) },
                        label = { Text(value.toUiText().asString()) }
                    )
                }
            }

            OutlinedTextField(
                value = state.title,
                onValueChange = { onAction(TransactionDetailsActions.UpdateTitle(it)) },
                label = { Text(stringResource(R.string.add_transaction_title_label)) },
                isError = state.invalidTitle,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { onAction(TransactionDetailsActions.UpdateDescription(it)) },
                label = { Text(stringResource(R.string.add_transaction_title_description)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = state.amount,
                onValueChange = { onAction(TransactionDetailsActions.UpdateAmount(it)) },
                label = { Text(stringResource(R.string.add_transaction_title_amount)) },
                isError = state.invalidAmount,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
            )

            LargeDropdownMenu(
                label = stringResource(R.string.add_transaction_title_interval),
                selectedIndex = state.financeIntervalIndex,
                items = FinanceInterval.entries.map { it.toUiText().asString() },
                onItemSelected = { index, _ ->
                    onAction(TransactionDetailsActions.UpdateFinanceInterval(index))
                },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
            )

            LargeDropdownMenu(
                label = stringResource(R.string.add_transaction_title_category),
                selectedIndex = state.financeCategoryIndex,
                items = if (state.financeTypeIndex == TransactionType.INCOME.ordinal) {
                    IncomeCategory.entries.map {
                        FinanceCategory.Income(it)
                    }
                } else {
                    ExpenseCategory.entries.map {
                        FinanceCategory.Expense(it)
                    }
                }.map { it.toUiText().asString() },
                onItemSelected = { index, _ ->
                    onAction(TransactionDetailsActions.UpdateFinanceCategory(index))
                },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.padding(32.dp))

        Button(
            onClick = { onAction(TransactionDetailsActions.SaveTransaction()) },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.save_button),
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionDetailsViewPreview() {
    MaterialTheme {
        Scaffold { innerPadding ->
            TransactionDetailsView(
                state = TransactionDetailsState(
                    error = DataError.Local.UNKNOWN.toUiText(),
                ),
                onAction = {},
                modifier = Modifier
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.surfaceDim)
            )
        }
    }
}