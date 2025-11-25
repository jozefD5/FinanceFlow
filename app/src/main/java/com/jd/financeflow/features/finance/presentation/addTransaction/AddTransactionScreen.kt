package com.jd.financeflow.features.finance.presentation.addTransaction

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

import com.jd.financeflow.R
import com.jd.financeflow.features.core.domain.finance.FinanceCategory
import com.jd.financeflow.features.core.domain.finance.FinanceType
import com.jd.financeflow.features.core.presentation.components.LargeDropdownMenu
import com.jd.financeflow.features.core.presentation.components.toUiText

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
fun AddTransactionView(
    state: AddTransactionState,
    onAction: (AddTransactionActions) -> Unit,
    modifier: Modifier = Modifier,
    focusManager: FocusManager = LocalFocusManager.current,
    scrollState: ScrollState = rememberScrollState()
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
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
                FinanceType.entries.forEachIndexed { index, value ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = FinanceType.entries.size
                        ),
                        selected = state.financeTypeIndex == index,
                        onClick = { onAction(AddTransactionActions.UpdateFinanceType(index)) },
                        label = { Text(value.toUiText().asString()) }
                    )
                }
            }

            TextField(
                value = state.title,
                onValueChange = { onAction(AddTransactionActions.UpdateTitle(it)) },
                label = { Text(stringResource(R.string.add_transaction_title_label)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )

            TextField(
                value = state.description,
                onValueChange = { onAction(AddTransactionActions.UpdateDescription(it)) },
                label = { Text(stringResource(R.string.add_transaction_title_description)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )

            TextField(
                value = state.amount,
                onValueChange = { onAction(AddTransactionActions.UpdateAmount(it)) },
                label = { Text(stringResource(R.string.add_transaction_title_amount)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )

            LargeDropdownMenu(
                label = stringResource(R.string.add_transaction_title_interval),
                items = if (state.financeTypeIndex == FinanceType.INCOME.ordinal) {
                    FinanceCategory.IncomeCategory.entries.map {
                        FinanceCategory.Income(it)
                    }
                } else {
                    FinanceCategory.ExpenseCategory.entries.map {
                        FinanceCategory.Expense(it)
                    }
                }.map { it.toUiText().asString() },
                onItemSelected = { index, _ ->
                    onAction(
                        AddTransactionActions.UpdateFinanceCategory(
                            index
                        )
                    )
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { onAction(AddTransactionActions.SaveTransaction) },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.save_button),
                modifier = Modifier.padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.padding(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun AddTransactionViewPreview() {
    MaterialTheme {
        Scaffold { innerPadding ->
            AddTransactionView(
                state = AddTransactionState(),
                onAction = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}