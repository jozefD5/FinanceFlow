package com.jd.financeflow.features.finance.presentation.financeDetails

import android.icu.util.Currency
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerState
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.jd.financeflow.features.finance.domain.models.FinanceType
import com.jd.financeflow.features.finance.presentation.financeDetails.components.DataPickerField

/**
 * Screen for adding or modifying finance transaction.
 *
 * @param modifier modifier to be applied
 * @param viewModel view model to be used
 */
@Composable
fun FinanceDetailsScreen(
    modifier: Modifier = Modifier,
    onSuccessfulAdd: () -> Unit = {},
    viewModel: FinanceDetailsViewModel = koinViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.value.startDate
    )

    FinanceDetailsView(
        state = state.value,
        datePickerState = datePickerState,
        onAction = { action ->
            val actualAction = when (action) {
                is FinanceDetailsActions.OnSaveFinance -> action
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
 * @param datePickerState date picker state to be used
 * @param onAction action to be performed
 * @param modifier modifier to be applied
 * @param focusManager focus manager to be used
 * @param scrollState scroll state to be used
 */
@Composable
fun FinanceDetailsView(
    state: FinanceDetailsState,
    datePickerState: DatePickerState,
    onAction: (FinanceDetailsActions) -> Unit,
    modifier: Modifier = Modifier,
    focusManager: FocusManager = LocalFocusManager.current,
    scrollState: ScrollState = rememberScrollState()
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(scrollState)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_small))
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            if (state.error != null) {
                Text(
                    text = state.error.asString(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .padding(bottom = dimensionResource(R.dimen.padding_small))
                    .fillMaxWidth()
            ) {
                FinanceType.entries.forEachIndexed { index, value ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = FinanceType.entries.size
                        ),
                        selected = state.financeTypeIndex == index,
                        onClick = { onAction(FinanceDetailsActions.OnUpdateFinanceType(index)) },
                        label = { Text(value.toUiText().asString()) }
                    )
                }
            }

            OutlinedTextField(
                value = state.title,
                onValueChange = { onAction(FinanceDetailsActions.OnUpdateTitle(it)) },
                label = { Text(stringResource(R.string.add_finance_title_label)) },
                isError = state.invalidTitle,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                shape = RoundedCornerShape(dimensionResource(R.dimen.round_corner_size_medium)),
                modifier = Modifier
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { onAction(FinanceDetailsActions.OnUpdateDescription(it)) },
                label = { Text(stringResource(R.string.add_finance_title_description)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                shape = RoundedCornerShape(dimensionResource(R.dimen.round_corner_size_medium)),
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = state.amount,
                onValueChange = { onAction(FinanceDetailsActions.OnUpdateAmount(it)) },
                label = { Text(stringResource(R.string.add_finance_title_amount)) },
                leadingIcon = {
                    Text(Currency.getInstance(state.currencyCode).symbol.toString())
                },
                isError = state.invalidAmount,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                shape = RoundedCornerShape(dimensionResource(R.dimen.round_corner_size_medium)),
                modifier = Modifier
                    .fillMaxWidth()
            )


            DataPickerField(
                onValueChange = { selectedDate ->
                    if (selectedDate != null) {
                        onAction(FinanceDetailsActions.OnUpdateStartDate(selectedDate))
                    }
                },
                label = stringResource(R.string.add_finance_title_start_date),
                placeholder = stringResource(R.string.add_finance_title_start_date_placeholder),
                shape = RoundedCornerShape(dimensionResource(R.dimen.round_corner_size_medium)),
                datePickerState = datePickerState,
                modifier = Modifier.fillMaxWidth()
            )


            LargeDropdownMenu(
                label = stringResource(R.string.add_finance_title_interval),
                selectedIndex = state.financeIntervalIndex,
                items = FinanceInterval.entries.map { it.toUiText().asString() },
                onItemSelected = { index, _ ->
                    onAction(FinanceDetailsActions.OnUpdateFinanceInterval(index))
                },
                shape = RoundedCornerShape(dimensionResource(R.dimen.round_corner_size_medium)),
                modifier = Modifier.fillMaxWidth()
            )

            LargeDropdownMenu(
                label = stringResource(R.string.add_finance_title_category),
                selectedIndex = state.financeCategoryIndex,
                items = if (state.financeTypeIndex == FinanceType.INCOME.ordinal) {
                    IncomeCategory.entries.map {
                        FinanceCategory.Income(it)
                    }
                } else {
                    ExpenseCategory.entries.map {
                        FinanceCategory.Expense(it)
                    }
                }.map { it.toUiText().asString() },
                onItemSelected = { index, _ ->
                    onAction(FinanceDetailsActions.OnUpdateFinanceCategory(index))
                },
                shape = RoundedCornerShape(dimensionResource(R.dimen.round_corner_size_medium)),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = { onAction(FinanceDetailsActions.OnSaveFinance()) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.padding_large))
        ) {
            Text(
                text = stringResource(R.string.save_button),
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_large)))
    }
}

@Preview(showBackground = true)
@Composable
fun FinanceDetailsViewPreview() {
    MaterialTheme {
        Scaffold { innerPadding ->
            FinanceDetailsView(
                state = FinanceDetailsState(
                    error = DataError.Local.UNKNOWN.toUiText(),
                ),
                datePickerState = rememberDatePickerState(),
                onAction = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.surfaceDim)
            )
        }
    }
}