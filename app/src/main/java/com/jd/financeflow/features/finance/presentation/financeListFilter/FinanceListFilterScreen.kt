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
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import com.jd.financeflow.features.finance.domain.models.FinanceType
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
        // Type selection (connected unit)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = 0,
                    count = FinanceType.entries.size,
                ),
                selected = state.selectedTypeIndex == 0,
                onClick = {
                    onAction(FinanceListFilterActions.OnUpdateType(0))
                },
                label = { Text(stringResource(R.string.filter_type_button_both_label)) }
            )

            FinanceType.entries.forEachIndexed { index, value ->
                val actualIndex = index + 1
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = actualIndex,
                        count = FinanceType.entries.size + 1,
                    ),
                    selected = state.selectedTypeIndex == actualIndex,
                    onClick = {
                        onAction(FinanceListFilterActions.OnUpdateType(actualIndex))
                    },
                    label = { Text(value.toUiText().asString()) }
                )
            }
        }

        // Category section
        OutlinedCard(
            shape = RoundedCornerShape(
                dimensionResource(R.dimen.padding_medium)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                // Header with Switch
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.filter_category_switch_label),
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    trailingContent = {
                        Switch(
                            enabled = state.selectedTypeIndex > 0,
                            checked = state.enableCategories,
                            onCheckedChange = {
                                onAction(
                                    FinanceListFilterActions.OnEnableCategories(!state.enableCategories)
                                )
                            }
                        )
                    }
                )

                // Category List (Animated Visibility makes it feel premium)
                AnimatedVisibility(visible = state.enableCategories && state.selectedTypeIndex > 0) {
                    Column {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        val currentCategories = if (state.selectedType == FinanceType.INCOME)
                            state.categoryIncomeOptions else state.categoryExpenseOptions

                        val checkList = if (state.selectedType == FinanceType.INCOME)
                            state.categoryIncomeSelectedOptions else state.categoryExpenseSelectedOptions

                        currentCategories.forEachIndexed { index, category ->
                            ListItem(
                                headlineContent = { Text(category.toUiText().asString()) },
                                trailingContent = {
                                    Checkbox(
                                        checked = checkList[index],
                                        onCheckedChange = {
                                            if (state.selectedType == FinanceType.INCOME) {
                                                onAction(
                                                    FinanceListFilterActions.OnCategorySelected(
                                                        FinanceType.INCOME,
                                                        index
                                                    )
                                                )
                                            } else {
                                                onAction(
                                                    FinanceListFilterActions.OnCategorySelected(
                                                        FinanceType.EXPENSE,
                                                        index
                                                    )
                                                )
                                            }
                                        }
                                    )
                                },
                                modifier = Modifier.clickable {
                                    if (state.selectedType == FinanceType.INCOME) {
                                        onAction(
                                            FinanceListFilterActions.OnCategorySelected(
                                                FinanceType.INCOME,
                                                index
                                            )
                                        )
                                    } else {
                                        onAction(
                                            FinanceListFilterActions.OnCategorySelected(
                                                FinanceType.EXPENSE,
                                                index
                                            )
                                        )
                                    }
                                },
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

                    RangeSlider(
                        value = state.selectedAmountRangeMini..state.selectedAmountRangeMax,
                        steps = 100,
                        valueRange = state.minimumAmount..state.maximumAmount,
                        onValueChange = {},
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
                    enableCategories = false,
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