package com.jd.financeflow.features.finance.presentation.financeDetails

import android.icu.util.Currency
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
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
import com.jd.financeflow.features.finance.domain.models.FinanceType
import com.jd.financeflow.features.finance.presentation.financeDetails.components.DataPickerField

@Composable
fun FinanceDetailsScreen(
    modifier: Modifier = Modifier,
    onSuccessfulAdd: () -> Unit = {},
    onBackNavigation: () -> Unit = {},
    viewModel: FinanceDetailsViewModel = koinViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    FinanceDetailsView(
        state = state.value,
        onAction = { action ->
            val actualAction = when (action) {
                is FinanceDetailsActions.OnSaveFinance -> action.copy(action = onSuccessfulAdd)
                is FinanceDetailsActions.OnBackNavigationClicked -> {
                    onBackNavigation()
                    action
                }

                else -> action
            }
            viewModel.onAction(actualAction)
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FinanceDetailsView(
    state: FinanceDetailsState,
    onAction: (FinanceDetailsActions) -> Unit,
    modifier: Modifier = Modifier,
    focusManager: FocusManager = LocalFocusManager.current,
    scrollState: ScrollState = rememberScrollState(),
) {
    Column(
        modifier = modifier
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.screen_title_transaction_details),
                    style = MaterialTheme.typography.titleMediumEmphasized.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                )
            },
            navigationIcon = {
                IconButton(onClick = { onAction(FinanceDetailsActions.OnBackNavigationClicked) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                        contentDescription = stringResource(R.string.content_description_back),
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
            ) {
                Spacer(Modifier.height(4.dp))

                // Error banner
                AnimatedVisibility(
                    visible = state.error != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    if (state.error != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.padding(12.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(20.dp),
                                )
                                Text(
                                    text = state.error.asString(),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                    ),
                                )
                            }
                        }
                    }
                }

                // Income / Expense type selector
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    FinanceType.entries.forEachIndexed { index, value ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = FinanceType.entries.size,
                            ),
                            selected = state.financeTypeIndex == index,
                            onClick = { onAction(FinanceDetailsActions.OnUpdateFinanceType(index)) },
                            label = { Text(value.toUiText().asString()) },
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = state.title,
                        onValueChange = { onAction(FinanceDetailsActions.OnUpdateTitle(it)) },
                        label = { Text(stringResource(R.string.add_finance_title_label)) },
                        isError = state.invalidTitle,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        ),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    OutlinedTextField(
                        value = state.description,
                        onValueChange = { onAction(FinanceDetailsActions.OnUpdateDescription(it)) },
                        label = { Text(stringResource(R.string.add_finance_title_description)) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        ),
                        maxLines = 3,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    OutlinedTextField(
                        value = state.amount,
                        onValueChange = { onAction(FinanceDetailsActions.OnUpdateAmount(it)) },
                        label = { Text(stringResource(R.string.add_finance_title_amount)) },
                        prefix = {
                            Text(Currency.getInstance(state.currencyCode).symbol.toString())
                        },
                        isError = state.invalidAmount,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        ),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    DataPickerField(
                        value = state.startDate,
                        onValueChange = { selectedDate ->
                            if (selectedDate != null) {
                                onAction(FinanceDetailsActions.OnUpdateStartDate(selectedDate))
                            }
                        },
                        label = stringResource(R.string.add_finance_title_start_date),
                        placeholder = stringResource(R.string.add_finance_title_start_date_placeholder),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    LargeDropdownMenu(
                        label = stringResource(R.string.add_finance_title_interval),
                        selectedIndex = state.financeIntervalIndex,
                        items = FinanceInterval.entries.map { it.toUiText().asString() },
                        onItemSelected = { index, _ ->
                            onAction(FinanceDetailsActions.OnUpdateFinanceInterval(index))
                        },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    LargeDropdownMenu(
                        label = stringResource(R.string.add_finance_title_category),
                        selectedIndex = state.financeCategoryIndex,
                        items = if (state.financeTypeIndex == FinanceType.INCOME.ordinal) {
                            IncomeCategory.entries.map { FinanceCategory.Income(it) }
                        } else {
                            ExpenseCategory.entries.map { FinanceCategory.Expense(it) }
                        }.map { it.toUiText().asString() },
                        onItemSelected = { index, _ ->
                            onAction(FinanceDetailsActions.OnUpdateFinanceCategory(index))
                        },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onAction(FinanceDetailsActions.OnSaveFinance()) },
                enabled = !state.isLoading,
                shapes = ButtonDefaults.shapes(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.save_title),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FinanceDetailsViewPreview() {
    MaterialTheme {
        FinanceDetailsView(
            state = FinanceDetailsState(
                error = DataError.Local.UNKNOWN.toUiText(),
            ),
            onAction = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
