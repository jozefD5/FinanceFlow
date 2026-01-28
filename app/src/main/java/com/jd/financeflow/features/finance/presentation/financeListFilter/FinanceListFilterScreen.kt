package com.jd.financeflow.features.finance.presentation.financeListFilter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import org.koin.androidx.compose.koinViewModel

import com.jd.financeflow.R
import com.jd.financeflow.features.core.presentation.components.LargeDropdownMenu
import com.jd.financeflow.features.core.presentation.components.formatCurrency
import com.jd.financeflow.features.finance.domain.models.FinanceType
import com.jd.financeflow.features.finance.domain.models.SortOrder
import com.jd.financeflow.features.finance.presentation.components.toUiText

/**
 * Screen for filtering finance transactions.
 *
 * @param modifier modifier to be applied
 * @param viewModel view model to be used
 */
@Composable
fun FinanceListFilterScreen(
    modifier: Modifier = Modifier,
    onApplyFilter: () -> Unit = {},
    viewModel: FinanceListFilterViewModel = koinViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    FinanceListFilterView(
        state = state.value,
        onAction = { action ->
            when (action) {
                is FinanceListFilterActions.OnApplyFilter -> onApplyFilter()
                else -> viewModel.onAction(action)
            }
        },
        modifier = modifier
    )
}

/**
 * View for filtering finance transactions.
 *
 * @param state state to be displayed
 * @param onAction action to be performed
 * @param modifier modifier to be applied
 * @param scrollState scroll state to be used
 */
@Composable
fun FinanceListFilterView(
    state: FinanceListFilterState,
    onAction: (FinanceListFilterActions) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_large))
            .verticalScroll(scrollState)
    ) {
        // Sort section
        OutlinedCard(
            shape = RoundedCornerShape(
                dimensionResource(R.dimen.padding_medium)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(R.string.filter_sort_switch_label),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
            )

            Column {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                LargeDropdownMenu(
                    selectedIndex = state.selectedSortIndex,
                    items = SortOrder.entries.map { it.toUiText().asString() },
                    onItemSelected = { index, _ ->
                        onAction(FinanceListFilterActions.OnUpdateSort(index))
                    },
                    shape = TextFieldDefaults.shape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }
        }

        // Type and category selection
        OutlinedCard(
            shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.filter_category_switch_label),
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = state.enableCategories,
                            onCheckedChange = {
                                onAction(FinanceListFilterActions.OnEnableCategories(!state.enableCategories))
                            }
                        )
                    }
                )

                // Integrated Segmented Button
                // We only show this if Categories are enabled to keep the UI clean
                AnimatedVisibility(visible = state.enableCategories) {
                    Column {
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            val totalButtons = FinanceType.entries.size + 1

                            // "Both" Option
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = totalButtons),
                                selected = state.selectedTypeIndex == 0,
                                onClick = { onAction(FinanceListFilterActions.OnUpdateType(0)) },
                                label = { Text(stringResource(R.string.filter_type_button_both_label)) }
                            )

                            FinanceType.entries.forEachIndexed { index, value ->
                                val actualIndex = index + 1
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(index = actualIndex, count = totalButtons),
                                    selected = state.selectedTypeIndex == actualIndex,
                                    onClick = { onAction(FinanceListFilterActions.OnUpdateType(actualIndex)) },
                                    label = { Text(value.toUiText().asString()) }
                                )
                            }
                        }

                        // Conditional Category List
                        if (state.selectedTypeIndex > 0) {
                            HorizontalDivider(modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp))

                            val currentType = state.selectedType ?: FinanceType.EXPENSE
                            val categories = if (currentType == FinanceType.INCOME)
                                state.categoryIncomeOptions else state.categoryExpenseOptions
                            val selections = if (currentType == FinanceType.INCOME)
                                state.categoryIncomeSelectedOptions else state.categoryExpenseSelectedOptions

                            categories.forEachIndexed { index, category ->
                                val toggleAction = FinanceListFilterActions.OnCategorySelected(currentType, index)
                                ListItem(
                                    headlineContent = { Text(category.toUiText().asString()) },
                                    trailingContent = {
                                        Checkbox(
                                            checked = selections[index],
                                            onCheckedChange = { onAction(toggleAction) }
                                        )
                                    },
                                    modifier = Modifier.clickable { onAction(toggleAction) }
                                )
                            }
                        } else {
                            // Feedback when "Both" is selected
                            Text(
                                text = "Select Income or Expense to filter by specific categories",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Amount section
        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(R.string.filter_amount_switch_label),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                trailingContent = {
                    Switch(
                        checked = state.enableAmountRange,
                        onCheckedChange = {
                            onAction(
                                FinanceListFilterActions.OnEnableAmountRange(!state.enableAmountRange)
                            )
                        }
                    )
                }
            )

            AnimatedVisibility(visible = state.enableAmountRange) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.padding_small))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = stringResource(R.string.filter_mini_range_label))
                            Text(
                                text = formatCurrency(
                                    state.selectedAmountRangeMini.toDouble(),
                                    state.currencyCode
                                )
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = stringResource(R.string.filter_max_range_label))
                            Text(
                                text = formatCurrency(
                                    state.selectedAmountRangeMax.toDouble(),
                                    state.currencyCode
                                )
                            )
                        }
                    }

                    RangeSlider(
                        value = state.selectedAmountRangeMini..state.selectedAmountRangeMax,
                        valueRange = state.minimumAmount..state.maximumAmount,
                        onValueChange = { range ->
                            onAction(
                                FinanceListFilterActions.OnUpdateAmountRange(
                                    range.start,
                                    range.endInclusive
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.padding_large))
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FinanceListFilterViewPreview() {
    MaterialTheme {
        Scaffold { innerPadding ->
            FinanceListFilterView(
                state = FinanceListFilterState(
                    selectedTypeIndex = 1,
                    selectedType = FinanceType.INCOME,
                    enableCategories = true,
                    enableAmountRange = true
                ),
                onAction = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}