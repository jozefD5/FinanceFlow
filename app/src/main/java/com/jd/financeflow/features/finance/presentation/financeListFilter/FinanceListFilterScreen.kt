package com.jd.financeflow.features.finance.presentation.financeListFilter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

import com.jd.financeflow.R
import com.jd.financeflow.features.core.presentation.components.formatCurrency
import com.jd.financeflow.features.core.domain.finance.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.FilterConfiguration
import com.jd.financeflow.features.core.domain.finance.FinanceCategory
import com.jd.financeflow.features.core.domain.finance.FinanceType
import com.jd.financeflow.features.core.domain.finance.IncomeCategory
import com.jd.financeflow.features.finance.presentation.components.toUiText

@Composable
fun FinanceListFilterScreen(
    modifier: Modifier = Modifier,
    onApplyFilter: (FilterConfiguration) -> Unit = {},
    onBackNavigation: () -> Unit = {},
    viewModel: FinanceListFilterViewModel = koinViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    FinanceListFilterView(
        state = state.value,
        onAction = { action ->
            val actualAction = when (action) {
                is FinanceListFilterActions.OnApplyFilter -> action.copy(callback = onApplyFilter)
                is FinanceListFilterActions.OnBackNavigationClicked -> {
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FinanceListFilterView(
    state: FinanceListFilterState,
    onAction: (FinanceListFilterActions) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            IconButton(onClick = { onAction(FinanceListFilterActions.OnBackNavigationClicked) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = null,
                )
            }
            Text(
                text = stringResource(R.string.screen_title_filter),
                style = MaterialTheme.typography.titleMediumEmphasized.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
        }

        // Scrollable content
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
        ) {
            Spacer(Modifier.height(4.dp))

            // ── Category section ──────────────────────────────────────────
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        ),
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.filter_category_switch_label),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                ),
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.enableCategories,
                                onCheckedChange = {
                                    onAction(
                                        FinanceListFilterActions.OnEnableCategories(
                                            !state.enableCategories
                                        )
                                    )
                                },
                            )
                        },
                    )

                    AnimatedVisibility(visible = state.enableCategories) {
                        Column {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                            SingleChoiceSegmentedButtonRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                            ) {
                                FinanceType.entries.forEachIndexed { index, value ->
                                    SegmentedButton(
                                        shape = SegmentedButtonDefaults.itemShape(
                                            index = index,
                                            count = FinanceType.entries.size,
                                        ),
                                        selected = state.selectedTypeIndex == index,
                                        onClick = {
                                            onAction(FinanceListFilterActions.OnUpdateType(index))
                                        },
                                        label = { Text(value.toUiText().asString()) },
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )

                            val currentType = FinanceType.entries[state.selectedTypeIndex]
                            val categories = if (currentType == FinanceType.INCOME) {
                                IncomeCategory.entries.map { FinanceCategory.Income(it) }
                            } else {
                                ExpenseCategory.entries.map { FinanceCategory.Expense(it) }
                            }
                            val selections = if (currentType == FinanceType.INCOME)
                                state.selectedIncomeCategories else state.selectedExpenseCategories

                            categories.forEachIndexed { index, category ->
                                val toggleAction = FinanceListFilterActions.OnCategorySelected(
                                    currentType,
                                    index,
                                )
                                ListItem(
                                    colors = ListItemDefaults.colors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                    ),
                                    headlineContent = {
                                        Text(
                                            text = category.toUiText().asString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    },
                                    trailingContent = {
                                        Checkbox(
                                            checked = selections[index],
                                            onCheckedChange = { onAction(toggleAction) },
                                        )
                                    },
                                    modifier = Modifier.clickable { onAction(toggleAction) },
                                )
                            }

                            if (!state.enableCategories) {
                                Text(
                                    text = stringResource(R.string.filter_type_both_note),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    ),
                                    modifier = Modifier.padding(16.dp),
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            }

            // ── Amount range section ──────────────────────────────────────
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        ),
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.filter_amount_switch_label),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                ),
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.enableAmountRange,
                                onCheckedChange = {
                                    onAction(
                                        FinanceListFilterActions.OnEnableAmountRange(
                                            !state.enableAmountRange
                                        )
                                    )
                                },
                            )
                        },
                    )

                    AnimatedVisibility(visible = state.enableAmountRange) {
                        Column {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = stringResource(R.string.filter_mini_range_label),
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.alpha(0.65f),
                                    )
                                    Text(
                                        text = formatCurrency(
                                            state.selectedAmountRangeMini.toDouble(),
                                            state.currencyCode,
                                        ),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                        ),
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = stringResource(R.string.filter_max_range_label),
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.alpha(0.65f),
                                    )
                                    Text(
                                        text = formatCurrency(
                                            state.selectedAmountRangeMax.toDouble(),
                                            state.currencyCode,
                                        ),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                        ),
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
                                            range.endInclusive,
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                    }
                }
            }

            // ── Action buttons ────────────────────────────────────────────
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    onClick = { onAction(FinanceListFilterActions.OnApplyFilter()) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.filter_apply_button_label),
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
                OutlinedButton(
                    onClick = { onAction(FinanceListFilterActions.OnResetFilter) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.filter_reset_button_label),
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FinanceListFilterViewPreview() {
    MaterialTheme {
        Scaffold { innerPadding ->
            FinanceListFilterView(
                state = FinanceListFilterState(
                    selectedTypeIndex = 0,
                    enableCategories = true,
                    enableAmountRange = true,
                ),
                onAction = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
    }
}
