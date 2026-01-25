package com.jd.financeflow.features.finance.presentation.financeListFilter

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
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
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(scrollState)
    ) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .padding(bottom = dimensionResource(R.dimen.padding_small))
                .fillMaxWidth()
        ) {
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

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        onAction(FinanceListFilterActions.OnEnableCategories(!state.enableCategories))
                    }
                )
        ) {
            Text(stringResource(R.string.filter_category_switch_label))

            Switch(
                checked = state.enableCategories,
                enabled = state.selectedTypeIndex > 0,
                onCheckedChange = {}
            )
        }


        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = dimensionResource(R.dimen.padding_medium))
        ) {
            if (state.selectedType != null && state.enableCategories && state.selectedTypeIndex > 0) {
                if (state.selectedType == FinanceType.INCOME) {
                    state.categoryIncomeOptions.forEachIndexed { index, category ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    onClick = {}
                                )
                        ) {
                            Text(category.toUiText().asString())
                            Checkbox(
                                checked = state.categoryIncomeSelectedOptions[index],
                                onCheckedChange = {}
                            )
                        }
                    }
                } else {
                    state.categoryExpenseOptions.forEachIndexed { index, category ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    onClick = {}
                                )
                        ) {
                            Text(category.toUiText().asString())
                            Checkbox(
                                checked = state.categoryExpenseSelectedOptions[index],
                                onCheckedChange = {}
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FinanceListFilterViewPreview() {
    MaterialTheme {
        FinanceListFilterView(
            state = FinanceListFilterState(
                selectedTypeIndex = 0,
                selectedType = FinanceType.INCOME,
                enableCategories = true
            ),
            onAction = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}