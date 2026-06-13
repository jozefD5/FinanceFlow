package com.jd.financeflow.features.finance.presentation.financeListFilter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
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
import com.jd.financeflow.features.core.presentation.components.formatCurrency
import com.jd.financeflow.features.finance.domain.models.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.FilterConfiguration
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceType
import com.jd.financeflow.features.finance.domain.models.IncomeCategory
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
    onApplyFilter: (FilterConfiguration) -> Unit = {},
    viewModel: FinanceListFilterViewModel = koinViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    FinanceListFilterView(
        state = state.value,
        onAction = { action ->
            val actualAction = when (action) {
                is FinanceListFilterActions.OnApplyFilter -> {
                    action.copy(callback = onApplyFilter)
                }

                else -> action
            }
            viewModel.onAction(actualAction)
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
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.list_items_spacing_medium)
            ),
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_large))

        ) {
            // Type and category selection
            OutlinedCard(
                shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium)),
                border = BorderStroke(width = 0.dp, color = Color.Transparent),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_small))
                ) {
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
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
                                    .padding(
                                        horizontal = dimensionResource(R.dimen.padding_large),
                                        vertical = dimensionResource(R.dimen.padding_small)
                                    )
                            ) {
                                FinanceType.entries.forEachIndexed { index, value ->
                                    SegmentedButton(
                                        shape = SegmentedButtonDefaults.itemShape(
                                            index = index,
                                            count = FinanceType.entries.size
                                        ),
                                        selected = state.selectedTypeIndex == index,
                                        onClick = {
                                            onAction(
                                                FinanceListFilterActions.OnUpdateType(index)
                                            )
                                        },
                                        label = { Text(value.toUiText().asString()) }
                                    )
                                }
                            }

                            // Conditional Category List
                            if (state.enableCategories) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(
                                        top = dimensionResource(R.dimen.padding_small),
                                        start = dimensionResource(R.dimen.padding_large),
                                        end = dimensionResource(R.dimen.padding_large)
                                    )
                                )
                                val currentType = FinanceType.entries[state.selectedTypeIndex]

                                val categories = if (currentType == FinanceType.INCOME) {
                                    IncomeCategory.entries.map { category ->
                                        FinanceCategory.Income(category)
                                    }
                                } else {
                                    ExpenseCategory.entries.map { category ->
                                        FinanceCategory.Expense(category)
                                    }
                                }

                                val selections = if (currentType == FinanceType.INCOME)
                                    state.selectedIncomeCategories else state.selectedExpenseCategories

                                categories.forEachIndexed { index, category ->
                                    val toggleAction =
                                        FinanceListFilterActions.OnCategorySelected(
                                            currentType,
                                            index
                                        )
                                    ListItem(
                                        colors = ListItemDefaults.colors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        ),
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
                                    text = stringResource(R.string.filter_type_both_note),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(
                                        dimensionResource(R.dimen.padding_large)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Amount section
            OutlinedCard(
                border = BorderStroke(width = 0.dp, color = Color.Transparent),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                ListItem(
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
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
                        HorizontalDivider(
                            modifier = Modifier.padding(
                                horizontal = dimensionResource(R.dimen.horizontal_divider_padding_medium)
                            )
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = dimensionResource(R.dimen.padding_small),
                                    horizontal = dimensionResource(R.dimen.padding_large)
                                )
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_large))
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        onAction(FinanceListFilterActions.OnApplyFilter())
                    }
                ) {
                    Text(
                        text = stringResource(R.string.filter_apply_button_label),
                        modifier = Modifier.padding(
                            vertical = dimensionResource(R.dimen.padding_small),
                            horizontal = dimensionResource(R.dimen.padding_small)
                        )
                    )
                }

                FilledTonalButton(
                    onClick = {
                        onAction(FinanceListFilterActions.OnResetFilter)
                    },
                ) {
                    Text(
                        text = stringResource(R.string.filter_reset_button_label),
                        modifier = Modifier.padding(
                            vertical = dimensionResource(R.dimen.padding_small)
                        )
                    )
                }
            }
            Spacer(Modifier.padding(dimensionResource(R.dimen.padding_large)))
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
                    selectedTypeIndex = 0,
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